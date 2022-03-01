package club.sk1er.patcher.util.status;

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
import java.net.UnknownHostException;

public class ProtocolVersionDetector {

    //#if MC==10809
    public static final ProtocolVersionDetector instance = new ProtocolVersionDetector();

    public boolean isCompatibleWithVersion(String ip, int version) {
        ServerAddress address = ServerAddress.fromString(ip);
        String addressIp = address.getIP();
        int addressPort = address.getPort();

        NetworkManager nm = null;
        try {
            nm = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(addressIp), addressPort, false);
            VersionCompatibilityStatusState handler = new VersionCompatibilityStatusState(version);
            nm.setNetHandler(handler);
            nm.sendPacket(new C00Handshake(version, addressIp, addressPort, EnumConnectionState.STATUS));
            nm.sendPacket(new C00PacketServerQuery());

            // Minecraft's network system requires you to tick the network manager every ~50 milliseconds.
            // The only way to avoid this without introducing a chance of getting a deadlock is to write our own protocol
            // system, which isn't worth it since we need to wait for the result anyways.
            while (handler.state == VersionCompatibilityStatusState.State.WAITING) {
                nm.checkDisconnected();
                if (nm.isChannelOpen()) nm.processReceivedPackets();
                try {
                    // we aren't busy waiting, just ticking
                    //noinspection BusyWait
                    Thread.sleep(50L);
                } catch (InterruptedException ignored) {
                }
            }

            return handler.state == VersionCompatibilityStatusState.State.COMPATIBLE;
        } catch (UnknownHostException e) {
            return false;
        } finally {
            if (nm != null && nm.isChannelOpen()) nm.closeChannel(new ChatComponentText("Done"));
        }
    }

    private static class VersionCompatibilityStatusState implements INetHandlerStatusClient {

        private final int version;
        private State state = State.WAITING;

        public VersionCompatibilityStatusState(int version) {
            this.version = version;
        }

        @Override
        public void handleServerInfo(S00PacketServerInfo packetIn) {
            this.state = packetIn.getResponse().getProtocolVersionInfo().getProtocol() >= this.version
                ? State.COMPATIBLE
                : State.INCOMPATIBLE;
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
            this.state = State.REJECTED;
        }

        private enum State {
            WAITING,
            REJECTED,
            COMPATIBLE,
            INCOMPATIBLE
        }
    }
    //#endif
}
