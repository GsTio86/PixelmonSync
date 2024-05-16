package me.gt86.sync.mixin;

import com.pixelmonmod.pixelmon.api.economy.BankAccountManager;
import com.pixelmonmod.pixelmon.api.storage.*;
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
        if (!(event.getPlayer() instanceof ServerPlayerEntity player)) return;
        UUID playerUUID = player.getUUID();
        PlayerPartyStorage partyStorage = this.getSaveAdapter().load(playerUUID, PlayerPartyStorage.class);
        if (partyStorage != null) {
            partyStorage.tryUpdatePlayerName();
            this.parties.put(playerUUID, partyStorage);
        }
        PCStorage pc = this.getSaveAdapter().load(playerUUID, PCStorage.class);
        if (pc != null) {
            pc.setPlayer(playerUUID, player.getName().getString());
            this.initializePCForPlayer(player, pc);
            this.pcs.put(playerUUID, pc);
        }
    }

    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUUID = event.getPlayer().getUUID();
        this.playersWithSyncedPCs.remove(playerUUID);
        PlayerPartyStorage party = this.parties.remove(playerUUID);
        PCStorage pc = this.pcs.remove(playerUUID);
        if (party != null)
            this.getSaveAdapter().save(party);
        if (pc != null)
            this.getSaveAdapter().save(pc);
    }
}
