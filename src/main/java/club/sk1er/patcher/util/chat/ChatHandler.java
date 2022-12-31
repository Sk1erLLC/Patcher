package club.sk1er.patcher.util.chat;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.GuiNewChatAccessor;
import cc.polyfrost.oneconfig.libs.universal.ChatColor;
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

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//#if MC==11202
//$$ import net.minecraft.util.text.ChatType;
//#endif

@SuppressWarnings("unused")
public class ChatHandler {

    private static final Map<Integer, ChatEntry> chatMessageMap = new HashMap<>();
    private static final Map<Integer, Set<ChatLine>> messagesForHash = new HashMap<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String chatTimestampRegex = "^(?:\\[\\d\\d:\\d\\d(:\\d\\d)?(?: AM| PM|)]|<\\d\\d:\\d\\d>) ";
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static int currentMessageHash = -1;
    private int ticks;

    //#if MC==10809
    @SubscribeEvent
    public void renderChat(RenderGameOverlayEvent.Chat event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT && PatcherConfig.chatPosition) {
            event.posY -= 12;
        }
    }
    //#endif

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ClientChatReceivedEvent event) {
        //#if MC==10809
        IChatComponent message = event.message;
        int type = event.type;
        int gameInfoType = 2;
        //#else
        //$$ ITextComponent message = event.getMessage();
        //$$ ChatType type = event.getType();
        //$$ ChatType gameInfoType = ChatType.GAME_INFO;
        //#endif
        if (PatcherConfig.timestamps && !message.getUnformattedText().trim().isEmpty() && type != gameInfoType) {
            String time = getCurrentTime();
            if (PatcherConfig.timestampsStyle == 0) {
                ChatComponentIgnored component = new ChatComponentIgnored(ChatColor.GRAY + "[" + time + "] " + ChatColor.RESET);
                //#if MC==10809
                component.appendSibling(event.message);
                event.message = component;
                //#else
                //$$ component.appendSibling(event.getMessage());
                //$$ event.setMessage(component);
                //#endif
            } else if (PatcherConfig.timestampsStyle == 1) {
                LinkedList<IChatComponent> queue = new LinkedList<>();
                //#if MC==10809
                queue.add(event.message);
                //#else
                //$$ queue.add(event.getMessage());
                //#endif

                while (!queue.isEmpty()) {
                    IChatComponent component = queue.remove();
                    List<IChatComponent> siblings = component.getSiblings();

                    if (siblings.isEmpty()) {
                        HoverEvent hoverEvent = component.getChatStyle().getChatHoverEvent();
                        if (hoverEvent == null) {
                            component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentIgnored(ChatUtilities.translate("&7Sent at &e" + time + "&7."))));
                        } else {
                            IChatComponent value = hoverEvent.getValue();
                            value.appendText("\n");
                            value.appendText(ChatUtilities.translate("&7Sent at &e" + time + "&7."));
                        }
                    } else {
                        queue.addAll(component.getSiblings());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (ticks++ >= 12000) {
                long time = System.currentTimeMillis();
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
    public void setChatMessageMap(ClientChatReceivedEvent event) {
        //#if MC==10809
        IChatComponent message = event.message;
        //#else
        //$$ ITextComponent message = event.getMessage();
        //#endif
        String clearMessage = cleanColor(message.getFormattedText()).trim();
        if (clearMessage.isEmpty() && PatcherConfig.removeBlankMessages) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void changeWorld(WorldEvent.Load event) {
        ticks = 0;
    }

    public static void appendMessageCounter(IChatComponent chatComponent, boolean refresh) {
        if ((Loader.isModLoaded("hychat") || Loader.isModLoaded("labymod")) || !PatcherConfig.compactChat) {
            return;
        }

        if (!refresh) {
            String message = cleanColor(chatComponent.getFormattedText()).trim();
            if (message.isEmpty() || isDivider(message)) {
                return;
            }

            currentMessageHash = getChatComponentHash(chatComponent);
            long currentTime = System.currentTimeMillis();

            if (!chatMessageMap.containsKey(currentMessageHash)) {
                chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
            } else {
                ChatEntry entry = chatMessageMap.get(currentMessageHash);
                if ((currentTime - entry.lastSeenMessageMillis) > (PatcherConfig.compactChatTime * 1000L)) {
                    chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
                } else {
                    boolean deleted = deleteMessageByHash(currentMessageHash);
                    if (!deleted) {
                        chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
                    } else {
                        entry.messageCount++;
                        entry.lastSeenMessageMillis = currentTime;
                        chatComponent.appendSibling(new ChatComponentIgnored(ChatColor.GRAY + " (" + decimalFormat.format(entry.messageCount) + ")"));
                    }
                }
            }

        }

    }

    public static void setChatLine_addToList(ChatLine line) {
        if (currentMessageHash != -1) {
            messagesForHash.computeIfAbsent(currentMessageHash, k -> new HashSet<>()).add(line);
        }
    }

    public static void resetMessageHash() {
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
            List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
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

        final List<ChatLine> chatLinesWrapped = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getDrawnChatLines();
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
        if (isDivider(cleanColor(prevLine.getChatComponent().getUnformattedText())) &&
            Math.abs(chatLine.getUpdatedCounter() - prevLine.getUpdatedCounter()) <= 2) {
            chatMessageList.remove(index);
        }

        if (index >= chatMessageList.size()) {
            return index;
        }

        final ChatLine nextLine = chatMessageList.get(index);
        if (isDivider(cleanColor(nextLine.getChatComponent().getUnformattedText())) &&
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
        List<Integer> siblingHashes = new ArrayList<>();
        for (IChatComponent sibling : chatComponent.getSiblings()) {
            if (!(sibling instanceof ChatComponentIgnored) && sibling instanceof ChatComponentStyle) {
                siblingHashes.add(getChatComponentHash(sibling));
            }
        }

        if (chatComponent instanceof ChatComponentIgnored) {
            return Objects.hash(siblingHashes);
        }

        String unformattedText = chatComponent.getUnformattedText();
        String cleanedMessage = unformattedText.replaceAll(chatTimestampRegex, "").trim();
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

    private static String cleanColor(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static String getCurrentTime() {
        String timestampsPattern = "[hh:mm a]";
        if (PatcherConfig.secondsOnTimestamps) timestampsPattern = "[hh:mm:ss a]";
        if (PatcherConfig.timestampsFormat == 1) {
            timestampsPattern = "[HH:mm]";
            if (PatcherConfig.secondsOnTimestamps) timestampsPattern = "[HH:mm:ss]";
        }

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(timestampsPattern));
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
