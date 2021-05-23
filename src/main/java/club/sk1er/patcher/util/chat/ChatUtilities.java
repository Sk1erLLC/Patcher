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

import gg.essential.api.EssentialAPI;
import gg.essential.universal.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;

public class ChatUtilities {

    public static void sendMessage(String message) {
        sendMessage(message, true);
    }

    public static void sendMessage(String message, boolean prefix) {
        sendMessageHelper(prefix ? translate("&e[Patcher] &r") + translate(message) : translate(message));
    }

    private static void sendMessageHelper(String message) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (player != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(translate(message)));
        }
    }

    public static void sendNotification(String notificationCategory, String chatMessage) {
        if (!EssentialAPI.getConfig().getDisableAllNotifications()) {
            EssentialAPI.getNotifications().push(notificationCategory, translate(chatMessage));
        } else {
            sendMessage(chatMessage);
        }
    }

    public static String translate(String message) {
        return ChatColor.Companion.translateAlternateColorCodes('&', message);
    }
}
