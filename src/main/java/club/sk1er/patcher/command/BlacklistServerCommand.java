package club.sk1er.patcher.command;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class BlacklistServerCommand extends CommandBase {
    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "blacklist";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/blacklist <server ip>";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender
     * @param args
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            ChatUtilities.sendMessage("&cPlease input the server ip to blacklist.");
            return;
        }

        String command = args[0];
        boolean blacklisted = Patcher.instance.addOrRemoveBlacklist(command);
        String color = blacklisted ? "&c" : "&a";
        ChatUtilities.sendMessage("Server &e\"" + command + "\" &ris " + color + (blacklisted ? "now" : "no longer") + " &rblacklisted from chat length extension.");
        Patcher.instance.saveBlacklistedServers();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
