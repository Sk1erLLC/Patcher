package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.RenderArrowHook;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class RenderManagerMixin_CancelArrowRender {
    @Inject(method = "renderDebugBoundingBox", at = @At("HEAD"), cancellable = true)
    public void patcher$cancelArrowBoundingBoxRendering(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entityIn instanceof EntityArrow && RenderArrowHook.cancelRendering((EntityArrow) entityIn)) {
            ci.cancel();
        }
    }
}
