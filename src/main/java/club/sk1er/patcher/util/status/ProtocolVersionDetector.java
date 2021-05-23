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

package club.sk1er.patcher.util.status;

import gg.essential.api.utils.Multithreading;
import kotlin.Pair;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolVersionDetector {

    public static final ProtocolVersionDetector instance = new ProtocolVersionDetector();
    private final Map<Pair<String, Integer>, CompletableFuture<Boolean>> futures = new ConcurrentHashMap<>();

    public CompletableFuture<Boolean> isCompatibleWithVersion(String ip, int version) {
        CompletableFuture<Boolean> cached = futures.get(new Pair<>(ip, version));
        if (cached != null) return cached;
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Pair<String, Integer> tuple = new Pair<>(ip, version);
        futures.put(tuple, future);
        Multithreading.runAsync(() -> {
            try {
                ServerAddress address = ServerAddress.fromString(ip);
                NetworkManager nm = NetworkManager.createNetworkManagerAndConnect(
                    InetAddress.getByName(address.getIP()),
                    address.getPort(),
                    false
                );
                nm.setNetHandler(new VersionCompatibilityStatusState(future, version, nm, () -> futures.remove(tuple)));
                nm.sendPacket(new C00Handshake(
                    version,
                    address.getIP(),
                    address.getPort(),
                    EnumConnectionState.STATUS
                ));
                nm.sendPacket(new C00PacketServerQuery());
            } catch (Exception e) {
                future.completeExceptionally(e);
                futures.remove(tuple);
            }
        });

        return future;
    }

    private static class VersionCompatibilityStatusState implements INetHandlerStatusClient {

        private final CompletableFuture<Boolean> future;
        private final int version;
        private final NetworkManager manager;
        private final Runnable onComplete;
        private boolean received;

        public VersionCompatibilityStatusState(CompletableFuture<Boolean> future, int version, NetworkManager manager, Runnable onComplete) {
            this.future = future;
            this.version = version;
            this.manager = manager;
            this.onComplete = onComplete;
        }

        @Override
        public void handleServerInfo(S00PacketServerInfo packetIn) {
            if (received) {
                manager.closeChannel(new ChatComponentText("Done checking protocol versions."));
                return;
            }

            received = true;
            future.complete(packetIn.getResponse().getProtocolVersionInfo().getProtocol() >= version);
            onComplete.run();
        }

        @Override
        public void handlePong(S01PacketPong packetIn) {

        }

        /**
         * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
         *
         * @param reason disconnect reason
         */
        @Override
        public void onDisconnect(IChatComponent reason) {
            if (future.isDone()) return;
            future.complete(false);
            onComplete.run();
        }
    }
}
