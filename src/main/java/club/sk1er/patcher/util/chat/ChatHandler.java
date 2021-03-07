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
import net.minecraft.client.gui.ChatLine;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
public class ChatHandler {

    private static final Map<Integer, ChatEntry> chatMessageMap = new HashMap<>();
    private static final Map<Integer, Set<ChatLine>> messagesForHash = new HashMap<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String chatTimestampRegex = "^(?:\\[\\d\\d:\\d\\d(?: AM| PM|)]|<\\d\\d:\\d\\d>) ";

    public static int currentMessageHash = -1;
    private int ticks;

    @SubscribeEvent
    public void renderChat(RenderGameOverlayEvent.Chat event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT && PatcherConfig.chatPosition) {
            event.posY -= 12;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (PatcherConfig.timestamps && !event.message.getUnformattedText().trim().isEmpty() && event.type != 2) {
            final String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(PatcherConfig.timestampsFormat == 0 ? "[hh:mm a]" : "[HH:mm]"));
            if (PatcherConfig.timestampsStyle == 0) {
                final ChatComponentIgnored component = new ChatComponentIgnored(ChatColor.GRAY + "[" + time + "] " + ChatColor.RESET);
                component.appendSibling(event.message);
                event.message = component;
            } else if (PatcherConfig.timestampsStyle == 1) {
                if (event.message.getChatStyle().getChatHoverEvent() == null) {
                    event.message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentIgnored(ChatUtilities.translate("&7Sent at &e" + time + "&7."))));
                } else {
                    // todo: expand to append upon siblings, cant figure out how to do so
                    final IChatComponent value = event.message.getChatStyle().getChatHoverEvent().getValue();
                    value.appendText("\n");
                    value.appendText(ChatUtilities.translate("&7Sent at &e" + time + "&7."));
                }
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (ticks++ >= 12000) {
                final long time = System.currentTimeMillis();
                for (Map.Entry<Integer, ChatEntry> entry : chatMessageMap.entrySet()) {
                    if ((time - entry.getValue().lastSeenMessageMillis) > (PatcherConfig.compactChatTime * 1000L)) {
                        messagesForHash.remove(entry.getKey());
                    }
                }

                ticks = 0;
            }
        }
    }

    @SubscribeEvent
    public void changeWorld(WorldEvent.Load event) {
        ticks = 0;
    }

    public static boolean setChatLineHead(IChatComponent chatComponent, boolean refresh) {
        if ((Loader.isModLoaded("hychat") || Loader.isModLoaded("labymod")) || !PatcherConfig.compactChat) {
            return true;
        }

        if (!refresh) {
            final String message = cleanColour(chatComponent.getFormattedText()).trim();
            if (message.isEmpty() && PatcherConfig.antiClearChat) {
                return false;
            }

            if (message.isEmpty() || isDivider(message)) {
                return true;
            }

            currentMessageHash = getChatComponentHash(chatComponent);
            final long currentTime = System.currentTimeMillis();

            if (!chatMessageMap.containsKey(currentMessageHash)) {
                chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
            } else {
                final ChatEntry entry = chatMessageMap.get(currentMessageHash);
                if ((currentTime - entry.lastSeenMessageMillis) > (PatcherConfig.compactChatTime * 1000L)) {
                    chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
                } else {
                    final boolean deleted = deleteMessageByHash(currentMessageHash);
                    if (!deleted) {
                        chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
                    } else {
                        entry.messageCount++;
                        entry.lastSeenMessageMillis = currentTime;
                        chatComponent.appendSibling(new ChatComponentIgnored(ChatColor.GRAY + " (" + entry.messageCount + ")"));
                    }
                }
            }

            return true;
        }

        return true;
    }

    public static void setChatLine_addToList(ChatLine line) {
        if (currentMessageHash != -1) {
            messagesForHash.computeIfAbsent(currentMessageHash, k -> new HashSet<>()).add(line);
        }
    }

    public static void setChatLineReturn() {
        currentMessageHash = -1;
    }

    private static boolean deleteMessageByHash(int hashCode) {
        if (!messagesForHash.containsKey(hashCode) || messagesForHash.get(hashCode).isEmpty()) {
            return false;
        }

        final Set<ChatLine> toRemove = messagesForHash.get(hashCode);
        messagesForHash.remove(hashCode);

        final int normalSearchLength = 100;
        final int wrappedSearchLength = 300;

        boolean removedMessage = false;
        {
            final List<ChatLine> chatLines = mc.ingameGUI.getChatGUI().chatLines;
            for (int index = 0; index < chatLines.size() && index < normalSearchLength; index++) {
                final ChatLine chatLine = chatLines.get(index);

                if (toRemove.contains(chatLine)) {
                    removedMessage = true;
                    chatLines.remove(index);
                    index--;

                    if (index < 0 || index >= chatLines.size()) {
                        continue;
                    }

                    index = getMessageIndex(chatLines, index, chatLine);
                } else if (PatcherConfig.consecutiveCompactChat) {
                    break;
                }
            }
        }

        if (!removedMessage) {
            return false;
        }

        final List<ChatLine> chatLinesWrapped = mc.ingameGUI.getChatGUI().drawnChatLines;
        for (int index = 0; index < chatLinesWrapped.size() && index < wrappedSearchLength; index++) {
            final ChatLine chatLine = chatLinesWrapped.get(index);
            if (toRemove.contains(chatLine)) {
                chatLinesWrapped.remove(index);
                index--;

                if (index <= 0 || index >= chatLinesWrapped.size()) {
                    continue;
                }

                index = getMessageIndex(chatLinesWrapped, index, chatLine);
            } else if (PatcherConfig.consecutiveCompactChat) {
                break;
            }
        }

        return true;
    }

    private static int getMessageIndex(List<ChatLine> chatMessageList, int index, ChatLine chatLine) {
        final ChatLine prevLine = chatMessageList.get(index);
        if (isDivider(cleanColour(prevLine.getChatComponent().getUnformattedText())) &&
            Math.abs(chatLine.getUpdatedCounter() - prevLine.getUpdatedCounter()) <= 2) {
            chatMessageList.remove(index);
        }

        if (index >= chatMessageList.size()) {
            return index;
        }

        final ChatLine nextLine = chatMessageList.get(index);
        if (isDivider(cleanColour(nextLine.getChatComponent().getUnformattedText())) &&
            Math.abs(chatLine.getUpdatedCounter() - nextLine.getUpdatedCounter()) <= 2) {
            chatMessageList.remove(index);
        }

        index--;

        return index;
    }

    private static int getChatStyleHash(ChatStyle style) {
        final HoverEvent hoverEvent = style.getChatHoverEvent();
        HoverEvent.Action hoverAction = null;
        int hoverChatHash = 0;

        if (hoverEvent != null) {
            hoverAction = hoverEvent.getAction();
            hoverChatHash = getChatComponentHash(hoverEvent.getValue());
        }

        return Objects.hash(style.getColor(),
            style.getBold(),
            style.getItalic(),
            style.getUnderlined(),
            style.getStrikethrough(),
            style.getObfuscated(),
            hoverAction, hoverChatHash,
            style.getChatClickEvent(),
            style.getInsertion());
    }

    private static int getChatComponentHash(IChatComponent chatComponent) {
        final List<Integer> siblingHashes = new ArrayList<>();
        for (IChatComponent sibling : chatComponent.getSiblings()) {
            if (!(sibling instanceof ChatComponentIgnored) && sibling instanceof ChatComponentStyle) {
                siblingHashes.add(getChatComponentHash(sibling));
            }
        }

        if (chatComponent instanceof ChatComponentIgnored) {
            return Objects.hash(siblingHashes);
        }

        final String unformattedText = chatComponent.getUnformattedText();
        final String cleanedMessage = unformattedText.replaceAll(chatTimestampRegex, "").trim();
        return Objects.hash(cleanedMessage, siblingHashes, getChatStyleHash(chatComponent.getChatStyle()));
    }

    private static boolean isDivider(String clean) {
        clean = clean.replaceAll(chatTimestampRegex, "").trim();
        boolean divider = true;
        if (clean.length() < 5) {
            divider = false;
        } else {
            for (int i = 0; i < clean.length(); i++) {
                final char c = clean.charAt(i);
                if (c != '-' && c != '=' && c != '\u25AC') {
                    divider = false;
                    break;
                }
            }
        }

        return divider;
    }

    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    static class ChatEntry {
        int messageCount;
        long lastSeenMessageMillis;

        ChatEntry(int messageCount, long lastSeenMessageMillis) {
            this.messageCount = messageCount;
            this.lastSeenMessageMillis = lastSeenMessageMillis;
        }
    }
}
