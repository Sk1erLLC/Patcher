package club.sk1er.patcher.mixins.bugfixes.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * When handling packets on the main thread, Minecraft doesn't check if
 * the connection is still open, causing future task errors to spam the log
 * after disconnecting from a server. This mixin checks if the connection is
 * open before trying to process a packet.
 */
@Mixin(targets = "net.minecraft.network.PacketThreadUtil$1")
public class PacketThreadUtilMixin_LogSpam {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V"))
    private void patcher$ignorePacketsFromClosedConnections(Packet packet, INetHandler handler) {
        if (handler instanceof NetHandlerPlayClient) {
            if (((NetHandlerPlayClient) handler).getNetworkManager().isChannelOpen()) {
                packet.processPacket(handler);
            }
        } else {
            packet.processPacket(handler);
        }
    }
}
