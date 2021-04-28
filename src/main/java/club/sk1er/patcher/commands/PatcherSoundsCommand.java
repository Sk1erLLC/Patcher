package club.sk1er.patcher.commands;

import club.sk1er.patcher.util.chat.ChatUtilities;
import net.modcore.api.commands.Command;
import net.modcore.api.commands.DefaultHandler;

// does not show up to users, only there to help people understand where the sounds feature
// has gone after updating from a version from 9 months ago lol
public class PatcherSoundsCommand extends Command {
    public PatcherSoundsCommand() {
        super("patchersounds", true, true);
    }

    @DefaultHandler
    public void handle() {
        ChatUtilities.sendNotification("Patcher", "This command has been moved to /patcher sounds");
    }
}
