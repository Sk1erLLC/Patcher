package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandRenderer.class)
public class ArmorStandRendererMixin_HideNametag {
    @Inject(method = "canRenderName", at = @At("HEAD"), cancellable = true)
    private void patcher$hideNametag(CallbackInfoReturnable<Boolean> cir) {
        if (PatcherConfig.betterHideGui && Minecraft.getMinecraft().gameSettings.hideGUI) {
            cir.setReturnValue(false);
        }
    }
}
