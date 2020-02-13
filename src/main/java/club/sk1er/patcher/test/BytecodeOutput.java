package club.sk1er.patcher.test;

import io.netty.channel.ChannelHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

public class BytecodeOutput extends FMLEmbeddedChannel {

    public BytecodeOutput(String channelName, Side source, ChannelHandler... handlers) {
        super(channelName, source, handlers);
    }

    public void cleanAttributes() {
        attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(null);
        attr(NetworkRegistry.NET_HANDLER).set(null);
        attr(NetworkDispatcher.FML_DISPATCHER).set(null);
    }
}
