package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.RenderArrowHook;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderArrow.class)
public class RenderArrowMixin_CancelRender {
    @Inject(method = "doRender(Lnet/minecraft/entity/projectile/EntityArrow;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (RenderArrowHook.cancelRendering(entity)) { // can't use accessors in mixins in 0.7.11, so use a hook instead
            ci.cancel();
        }
    }
}
