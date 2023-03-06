package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.asm.external.mods.optifine.RenderTransformer;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSnowball.class)
public class RenderSnowballMixin_CleanProjectiles<T extends Entity> {

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void patcher$cleanProjectiles(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (PatcherConfig.cleanProjectiles && entity.ticksExisted < 2) ci.cancel();
    }

    //#if MC==10809
    @ModifyArg(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 1), index = 0)
    public float patcher$fixPerspectiveRendering(float x) {
        return RenderTransformer.checkPerspective() * x;
    }
    //#endif
}
