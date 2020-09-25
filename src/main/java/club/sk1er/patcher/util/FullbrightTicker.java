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

package club.sk1er.patcher.util;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("unused")
public class FullbrightTicker {

    public static boolean isFullbright() {
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isCallingFromMinecraftThread()) {
            return false;
        }

        return PatcherConfig.fullbright;
    }
}
