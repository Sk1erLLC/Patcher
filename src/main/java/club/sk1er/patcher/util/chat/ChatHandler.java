/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.chat;

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class ChatHandler {

    private final LinkedList<ChatEntry> entries = new LinkedList<>();
    private GuiNewChat chat;
    private int line;
    private int lastAmount = 0;

    @SubscribeEvent
    public void renderChat(RenderGameOverlayEvent.Chat event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT && PatcherConfig.chatPosition) {
            event.posY -= 12;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (chat == null) {
            chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        }

        if (!event.isCanceled() && event.type == 0) {
            final String message = event.message.getUnformattedText().trim();
            if (message.isEmpty() && PatcherConfig.antiClearChat) {
                event.setCanceled(true);
                return;
            }

            final String timeFormat = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    PatcherConfig.timestampsFormat == 0 ? "[hh:mm a]" : "[HH:mm]"
                )
            );

            if (PatcherConfig.compactChat) {
                if (message.isEmpty() || message.startsWith("---------") || message.startsWith("=========") || message.startsWith("▬▬▬▬▬")) {
                    return;
                }

                if (lastAmount != PatcherConfig.superCompactChatAmount) {
                    lastAmount = PatcherConfig.superCompactChatAmount;
                    entries.clear();
                }

                // If the last message sent is the same as the newly posted message
                ChatEntry print = null;
                for (ChatEntry entry : entries) {
                    if (entry.text.equals(message) || (entry.noSpace.length() == 0 && message.replace(" ", "").length() == 0)) {
                        chat.deleteChatLine(entry.id);
                        entry.amount++;
                        event.message.appendText(ChatColor.GRAY + " (" + entry.amount + ")");
                        print = entry;
                        break;
                    }
                }

                if (print == null) {
                    final ChatEntry entry = new ChatEntry(message.replace(" ", "").length() == 0 ? "" : message, 1, line);
                    entries.add(entry);
                    print = entry;
                    if (entries.size() > PatcherConfig.superCompactChatAmount) {
                        entries.removeFirst();
                    }
                } else {
                    entries.remove(print); //Push to end
                    entries.add(print);
                }

                if (PatcherConfig.timestamps) {
                    final ChatComponentText time = new ChatComponentText(ChatColor.GRAY + "[" + timeFormat + "] ");
                    time.appendSibling(event.message);
                    event.message = time;
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
                final ChatComponentText time = new ChatComponentText(ChatColor.GRAY + "[" + timeFormat + "] ");
                time.appendSibling(event.message);
                event.message = time;
            }
        }
    }

    static class ChatEntry {
        String text;
        int amount;
        int id;
        String noSpace;

        public ChatEntry(String text, int amount, int id) {
            this.text = text;
            this.amount = amount;
            this.id = id;
            noSpace = text.replace(" ", "");
        }
    }
}
