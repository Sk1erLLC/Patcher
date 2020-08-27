package club.sk1er.patcher.command;

import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

public class PatcherSoundsCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "patchersounds";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ChatUtilities.sendMessage("&cThis command has moved to &b/patcher sounds&c. This message will be removed in Patcher 1.4.");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("psounds");
    }
}
