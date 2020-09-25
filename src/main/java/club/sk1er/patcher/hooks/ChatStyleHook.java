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

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatStyleHook {

    public static HoverEvent getChatHoverEvent(ChatStyle chatComponent) {
        HoverEvent hoverEvent = chatComponent.chatHoverEvent == null ? chatComponent.getParent().getChatHoverEvent() : chatComponent.chatHoverEvent;
        if (!PatcherConfig.safeChatClicks) {
            return hoverEvent;
        }

        ClickEvent chatClickEvent = chatComponent.chatClickEvent;
        if (chatClickEvent == null) {
            return hoverEvent;
        }

        ClickEvent.Action action = chatClickEvent.getAction();

        if (!(action.equals(ClickEvent.Action.OPEN_FILE) || action.equals(ClickEvent.Action.OPEN_URL) || action.equals(ClickEvent.Action.RUN_COMMAND))) {
            return hoverEvent;
        }

        String msg = EnumChatFormatting.YELLOW.toString() + (action == ClickEvent.Action.RUN_COMMAND ? "Runs " : "Opens ") + EnumChatFormatting.AQUA + chatClickEvent.getValue() + EnumChatFormatting.YELLOW.toString() + " on click";
        if (hoverEvent == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg));
        }

        if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
            ChatComponentText append = new ChatComponentText(msg);
            IChatComponent value = hoverEvent.getValue();

            if (value.getSiblings().contains(append) || value.getFormattedText().contains(msg)) {
                return hoverEvent;
            }

            IChatComponent copy = value.createCopy();
            copy.appendText("\n");
            copy.appendText(msg);
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, copy);
        }

        return hoverEvent;
    }
}
