package club.sk1er.patcher.commands;

import net.minecraft.client.Minecraft;
import net.modcore.api.commands.Command;
import net.modcore.api.commands.DefaultHandler;

// dont want it to show up in /patcher help
public class DeleteNameHistoryCommand extends Command {
    public DeleteNameHistoryCommand() {
        super("$deletenamehistory", true, true);
    }

    @DefaultHandler
    public void handle() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(PatcherCommand.randomChatMessageId);
    }
}
