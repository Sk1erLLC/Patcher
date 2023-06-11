package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
//#if MC==10809
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
//#endif
//#if MC==11202
//$$ import net.minecraft.network.play.server.SPacketSpawnPlayer;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
public abstract class NetHandlerPlayClient_FixHandleSpawnPlayerNPE {

    @Shadow
    public abstract NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_);

    @Inject(
        method = "handleSpawnPlayer",
        cancellable = true,
        at =
        @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V",
            shift = At.Shift.AFTER
        ))
    //#if MC==10809
    private void patcher$fixHandleSpawnPlayerNPE(S0CPacketSpawnPlayer packetIn, CallbackInfo ci) {
        if (this.getPlayerInfo(packetIn.getPlayer()) == null) {
            ci.cancel();
        }
    }
    //#endif

    //#if MC==11202
    //$$ private void patcher$fixHandleSpawnPlayerNPE(SPacketSpawnPlayer packetIn, CallbackInfo ci) {
    //$$     if (this.getPlayerInfo(packetIn.getUniqueId()) == null) {
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //#endif

}
