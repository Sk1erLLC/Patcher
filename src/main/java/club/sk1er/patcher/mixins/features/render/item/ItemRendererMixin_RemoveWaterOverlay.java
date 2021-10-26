package club.sk1er.patcher.mixins.features.render.item;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin_RemoveWaterOverlay {
    @Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
    private void patcher$removeWaterOverlay(CallbackInfo ci) {
        if (PatcherConfig.removeWaterOverlay) {
            ci.cancel();
        }
    }
}
