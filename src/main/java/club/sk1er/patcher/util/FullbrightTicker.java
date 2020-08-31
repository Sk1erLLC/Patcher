package club.sk1er.patcher.util;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

public class FullbrightTicker {


    public static boolean isFullbright() {
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isCallingFromMinecraftThread()) {
            return false;
        }
        return PatcherConfig.fullbright;
    }


}
