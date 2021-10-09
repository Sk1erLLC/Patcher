package club.sk1er.patcher.mixins.bugfixes.network;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin_SignChatSpam {
    @Redirect(
        method = "handleUpdateSign",
        slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0)
    )
    private void patcher$removeDebugMessage(EntityPlayerSP instance, IChatComponent component) {
        // No-op
    }
}
