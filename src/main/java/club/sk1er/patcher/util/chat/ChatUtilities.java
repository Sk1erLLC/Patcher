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

import club.sk1er.mods.core.config.ModCoreConfig;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.universal.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

public class ChatUtilities {

    public static void sendMessage(String message) {
        sendMessage(message, true);
    }

    public static void sendMessage(String message, boolean prefix) {
        sendMessageHelper(prefix ? color("&e[Patcher] &r") + color(message) : color(message));
    }

    private static void sendMessageHelper(String message) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (player != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(color(message)));
        }
    }

    public static void sendNotification(String notificationCategory, String chatMessage) {
        if (!ModCoreConfig.INSTANCE.getDisableAllNotifications()) {
            Notifications.INSTANCE.pushNotification(notificationCategory, color(chatMessage));
        } else {
            sendMessage(chatMessage);
        }
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
