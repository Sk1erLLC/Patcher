package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin_InstantDimensionSwapping {
    @ModifyArg(
        method = {"handleJoinGame", "handleRespawn"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V")
    )
    private GuiScreen patcher$skipTerrainScreen(GuiScreen original) {
        return null;
    }
}
