package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_SkipTwitchRendering {
    @Inject(method = "renderStreamIndicator", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelStreamIndicator(CallbackInfo ci) {
        ci.cancel();
    }
}
