package me.gt86.sync.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import me.gt86.sync.connection.Database;
import me.gt86.sync.utils.ThreadUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;

public class ReloadCommand extends PixelCommand {

    public ReloadCommand(CommandDispatcher<CommandSource> dispatcher) {
        this(dispatcher, "reloadpixelmonsync", "/reloadpixelmonsync", 4);
    }

    public ReloadCommand(CommandDispatcher<CommandSource> dispatcher, String name, String usage, int permissionLevel) {
        super(dispatcher, name, usage, permissionLevel);
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException {
        PixelmonCommandUtils.sendMessage(sender, "Reloading PixelmonSync ...");
        ThreadUtil.runAsync( () -> {
            Database.init();
            PixelmonCommandUtils.sendMessage(sender, "PixelmonSync database reloaded successfully.");
        });
    }
}
