package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArrow.class)
public class LayerArrowMixin_CancelRender {
    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(CallbackInfo ci) {
        if (PatcherConfig.disableAttachedArrows) {
            ci.cancel();
        }
    }
}
