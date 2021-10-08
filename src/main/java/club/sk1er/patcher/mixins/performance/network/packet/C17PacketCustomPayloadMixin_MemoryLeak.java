package club.sk1er.patcher.mixins.performance.network.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(C17PacketCustomPayload.class)
public class C17PacketCustomPayloadMixin_MemoryLeak {
    @Shadow private PacketBuffer data;

    @Inject(method = "processPacket(Lnet/minecraft/network/play/INetHandlerPlayServer;)V", at = @At("TAIL"))
    private void patcher$releaseData(INetHandlerPlayServer handler, CallbackInfo ci) {
        if (this.data != null) {
            this.data.release();
        }
    }
}
