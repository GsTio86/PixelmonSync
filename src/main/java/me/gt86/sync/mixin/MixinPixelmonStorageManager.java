package me.gt86.sync.mixin;

import com.pixelmonmod.pixelmon.api.economy.BankAccountManager;
import com.pixelmonmod.pixelmon.api.storage.*;
import me.gt86.sync.PixelmonSync;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(value = PixelmonStorageManager.class, remap = false)
public abstract class MixinPixelmonStorageManager implements StorageManager, BankAccountManager {

    @Shadow
    @Final
    protected Map<UUID, PlayerPartyStorage> parties;

    @Shadow
    @Final
    protected Map<UUID, PCStorage> pcs;

    @Shadow
    @Final
    protected List<UUID> playersWithSyncedPCs;

    @Shadow
    public abstract StorageSaveAdapter getSaveAdapter();

    @Overwrite
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        UUID uuid = event.getPlayer().getUUID();
        if (this.parties.containsKey(uuid)) {
            this.parties.get(uuid).tryUpdatePlayerName();
        } else {
            getParty(uuid);
        }
        if (this.pcs.containsKey(uuid)) {
            PCStorage pc = this.pcs.get(uuid);
            pc.setPlayer(uuid, event.getPlayer().getName().getString());
            for (ServerPlayerEntity player : pc.getPlayersToUpdate()) {
                this.initializePCForPlayer(player, pc);
            }
        } else {
            getPCForPlayer(uuid);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLoadPlayerFile(PlayerEvent.LoadFromFile event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;
        UUID uuid = event.getPlayer().getUUID();
        PlayerPartyStorage partyStorage = this.getSaveAdapter().load(uuid, PlayerPartyStorage.class);
        if (partyStorage != null) {
            this.parties.put(uuid, partyStorage);
            PixelmonSync.sendDebugMessage("Loaded party for " + uuid);
        }
        PCStorage pc = this.getSaveAdapter().load(uuid, PCStorage.class);
        if (pc != null) {
            this.pcs.put(uuid, pc);
            PixelmonSync.sendDebugMessage("Loaded PC for " + uuid);
        }
    }

    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUUID = event.getPlayer().getUUID();
        this.playersWithSyncedPCs.remove(playerUUID);
        PlayerPartyStorage party = this.parties.remove(playerUUID);
        PCStorage pc = this.pcs.remove(playerUUID);
        if (party != null) {
            this.getSaveAdapter().save(party);
            PixelmonSync.sendDebugMessage("Saved party for " + playerUUID);
        }
        if (pc != null) {
            this.getSaveAdapter().save(pc);
            PixelmonSync.sendDebugMessage("Saved PC for " + playerUUID);
        }
    }
}
