package club.sk1er.patcher.util.chat;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHandler {

    private String lastMessage = "";
    private int line, amount;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (PatcherConfig.compactChat && !event.isCanceled() && event.type == 0) {
            // Get the chat instance
            GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

            // If the last message sent is the same as the newly posted message
            if (lastMessage.equals(event.message.getUnformattedText())) {

                // Delete the message
                chat.deleteChatLine(line);

                // Increase the amount of times it's been sent
                amount++;

                // Set the last message to be the newly posted message
                lastMessage = event.message.getUnformattedText();

                // Append (amount of times it's been sent) to the last message
                event.message.appendText(EnumChatFormatting.GRAY + " (" + amount + ")");
            } else {

                // Otherwise it's never been sent
                amount = 1;

                // Set the last message to be the newly posted message
                lastMessage = event.message.getUnformattedText();
            }

            // Increase the line the message was on
            line++;

            // Check if the event wasn't cancelled
            if (!event.isCanceled()) {
                // Print the chat message and allow it to be deleted
                chat.printChatMessageWithOptionalDeletion(event.message, line);
            }

            // If the message has been sent 256 times
            if (line > 256) {
                line = 0; // Set it to 0 again
            }

            // Cancel the message
            event.setCanceled(true);
        }
    }
}
