package club.sk1er.patcher.util.chat;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHandler {

    private String lastMessage = "";
    private int line, amount;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (!event.isCanceled() && event.type == 0) {
            String timeFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("[hh:mm a]"));
            if (PatcherConfig.compactChat) {
                String message = event.message.getUnformattedText();
                if (message.equals("") || message.startsWith("---------") || message.startsWith("=========")) {
                    return; // die!
                }

                // Get the chat instance
                GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

                // If the last message sent is the same as the newly posted message
                if (lastMessage.equals(message)) {

                    // Delete the message
                    chat.deleteChatLine(line);

                    // Increase the amount of times it's been sent
                    ++amount;

                    // Set the last message to be the newly posted message
                    lastMessage = message;

                    // Append (amount of times it's been sent) to the last message
                    event.message.appendText(EnumChatFormatting.GRAY + " (" + amount + ")");
                    if (PatcherConfig.timestamps) {
                        ChatComponentText newThing = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timeFormat + "] ");
                        newThing.appendSibling(event.message);
                        event.message = newThing;
                    }
                } else {

                    // Otherwise it's never been sent
                    amount = 1;

                    // Set the last message to be the newly posted message
                    lastMessage = message;
                    if (PatcherConfig.timestamps) {
                        ChatComponentText newThing = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timeFormat + "] ");
                        newThing.appendSibling(event.message);
                        event.message = newThing;
                    }
                }

                // Increase the line the message was on
                ++line;

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
            } else if (PatcherConfig.timestamps) {
                ChatComponentText newThing = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timeFormat + "] ");
                newThing.appendSibling(event.message);
                event.message = newThing;
            }
        }
    }
}
