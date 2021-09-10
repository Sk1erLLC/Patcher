package club.sk1er.patcher.mixins.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(S2EPacketCloseWindow.class)
public class S2EPacketCloseWindowMixin_NoCloseMyChat {
    @Inject(method = "processPacket", at = @At("HEAD"), cancellable = true)
    private void patcher$noCloseMyChat(INetHandlerPlayClient handler, CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            ci.cancel();
        }
    }
}
