package club.sk1er.patcher.util.chat;

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.mods.core.util.MinecraftUtils;

public class ChatUtilities {

    public static void sendMessage(String message) {
        MinecraftUtils.sendMessage(color("&e[Patcher] &r"), color(message));
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
