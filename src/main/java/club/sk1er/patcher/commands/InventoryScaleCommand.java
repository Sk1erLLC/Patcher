package club.sk1er.patcher.commands;

import club.sk1er.patcher.util.chat.ChatUtilities;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class InventoryScaleCommand extends Command {
    public InventoryScaleCommand() {
        super("invscale", true, true);
    }

    @DefaultHandler
    public void handle() {
        ChatUtilities.sendNotification("Patcher", "This command has been moved to /patcher invscale");
    }

    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        return Collections.singleton(new Alias("inventoryscale", true));
    }
}
