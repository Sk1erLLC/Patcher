package club.sk1er.patcher.commands;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import net.minecraft.client.Minecraft;

public class DeleteNameHistoryCommand extends Command {
    public DeleteNameHistoryCommand() {
        super("$deletenamehistory", true, true);
    }

    @DefaultHandler
    public void handle() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().deleteChatLine(PatcherCommand.randomChatMessageId);
    }
}
