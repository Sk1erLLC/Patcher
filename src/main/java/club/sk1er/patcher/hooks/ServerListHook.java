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

import club.sk1er.patcher.tweaker.asm.ServerListTransformer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link ServerListTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class ServerListHook {

    private static final Logger logger = LogManager.getLogger("Patcher - ServerList");

    public static ServerData getServerData(ServerList list, int index) {
        try {
            return list.servers.get(index);
        } catch (Exception e) {
            logger.error("Failed to get server data.", e);
            return null;
        }
    }

    public static void removeServerData(ServerList list, int index) {
        try {
            list.servers.remove(index);
        } catch (Exception e) {
            logger.error("Failed to remove server data.", e);
        }
    }

    public static void addServerData(ServerList list, ServerData index) {
        try {
            list.servers.add(index);
        } catch (Exception e) {
            logger.error("Failed to add server data.", e);
        }
    }

    public static void swapServers(ServerList list, int pos1, int pos2) {
        try {
            ServerData serverData = list.getServerData(pos1);
            list.servers.set(pos1, list.getServerData(pos2));
            list.servers.set(pos2, serverData);
            list.saveServerList();
        } catch (Exception e) {
            logger.error("Failed to swap servers.", e);
        }
    }

    public static void set(ServerList list, int index, ServerData sever) {
        try {
            list.servers.set(index, sever);
        } catch (Exception e) {
            logger.error("Failed to set server data.", e);
        }
    }
}
