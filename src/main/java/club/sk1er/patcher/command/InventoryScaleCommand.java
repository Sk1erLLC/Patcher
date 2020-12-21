package club.sk1er.patcher.command;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class InventoryScaleCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "inventoryscale";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <scaling>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            ChatUtilities.sendNotification("Inventory Scale", "Too many arguments were provided. Usage: " + getCommandUsage(sender) + ".");
        } else if (args.length < 1) {
            ChatUtilities.sendNotification("Inventory Scale", "Too little arguments were provided. Usage: " + getCommandUsage(sender) + ".");
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                ChatUtilities.sendMessage("             &eInventory Scale", false);
                ChatUtilities.sendMessage("&7Usage: /inventoryscale <scaling>", false);
                ChatUtilities.sendMessage("&7Scaling may be a number between 1-5, or", false);
                ChatUtilities.sendMessage("&7small/normal/large/auto", false);
                ChatUtilities.sendMessage("&7Use '/inventoryscale off' to disable scaling.", false);
                return;
            }

            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("none")) {
                PatcherConfig.desiredScaleOverride = -1;
                ChatUtilities.sendNotification("Inventory Scale", "Disabled inventory scaling.");
                Patcher.instance.forceSaveConfig();
                return;
            }

            int scaling;
            if (args[0].equalsIgnoreCase("small") || args[0].equalsIgnoreCase("s")) {
                scaling = 1;
            } else if (args[0].equalsIgnoreCase("normal") || args[0].equalsIgnoreCase("n")) {
                scaling = 2;
            } else if (args[0].equalsIgnoreCase("large") || args[0].equalsIgnoreCase("l")) {
                scaling = 3;
            } else if (args[0].equalsIgnoreCase("auto") || args[0].equalsIgnoreCase("a")) {
                scaling = 5;
            } else {
                try {
                    scaling = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    ChatUtilities.sendNotification("Inventory Scale", "Invalid scaling identifier. Use '/inventoryscale help' for assistance.");
                    return;
                }
            }

            if (scaling < 1) {
                PatcherConfig.desiredScaleOverride = -1;
                ChatUtilities.sendNotification("Inventory Scale", "Disabled inventory scaling.");
                Patcher.instance.forceSaveConfig();
                return;
            } else if (scaling > 5) {
                ChatUtilities.sendNotification("Inventory Scale", "Invalid scaling. Must be between 1-5.");
                return;
            }

            ChatUtilities.sendNotification("Inventory Scale", "Set inventory scaling to " + scaling);
            PatcherConfig.desiredScaleOverride = scaling;
            Patcher.instance.forceSaveConfig();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("invscale", "scale");
    }
}
