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
import java.util.LinkedList;

public class ChatHandler {

//    private String lastMessage = "";
//    private int line, amount;
    private LinkedList<ChatEntry> entries = new LinkedList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (!event.isCanceled() && event.type == 0) {
            String timeFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("[hh:mm a]"));
            if (PatcherConfig.compactChat) {
                String message = event.message.getUnformattedText();
                if (message.isEmpty() || message.startsWith("---------") || message.startsWith("=========")) {
                    return; // die!
                }

                // Get the chat instance
                GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

                // If the last message sent is the same as the newly posted message
                ChatEntry print = null;
                for (ChatEntry entry : entries) {
                    if(entry.text.equalsIgnoreCase(message)) {
                        chat.deleteChatLine(entry.id);
                        entry.amount++;
                        event.message.appendText(EnumChatFormatting.GRAY + " (" + entry.amount + ")");
                        print = entry;
                        break;
                    }
                }
                if(print == null) {
                    ChatEntry e = new ChatEntry(message, 1, line);
                    entries.add(e);
                    print = e;
                    if(entries.size() > 10)
                        entries.removeLast();
                } else {
                    entries.remove(print); //Push to front
                    entries.add(print);
                }

                if (PatcherConfig.timestamps) {
                    ChatComponentText newThing = new ChatComponentText(EnumChatFormatting.GRAY + "[" + timeFormat + "] ");
                    newThing.appendSibling(event.message);
                    event.message = newThing;
                }
                // Increase the line the message was on
                ++line;

                // Check if the event wasn't cancelled
                if (!event.isCanceled()) {
                    // Print the chat message and allow it to be deleted
                    chat.printChatMessageWithOptionalDeletion(event.message, print.id);
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
    private int line;

    class ChatEntry {
        String text;
        int amount;
        int id;
        private long lastAccessed;
        public ChatEntry(String text, int amount, int id) {
            this.text = text;
            this.amount = amount;
            this.id = id;
            lastAccessed = System.currentTimeMillis();
        }
    }
}
