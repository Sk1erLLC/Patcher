package club.sk1er.patcher.commands;

import club.sk1er.patcher.util.chat.ChatUtilities;
import net.modcore.api.commands.Command;
import net.modcore.api.commands.DefaultHandler;

public class InventoryScaleCommand extends Command {
    public InventoryScaleCommand() {
        super("invscale", true, true);
    }

    @DefaultHandler
    public void handle() {
        ChatUtilities.sendNotification("Patcher", "This command has been moved to /patcher invscale");
    }
}
