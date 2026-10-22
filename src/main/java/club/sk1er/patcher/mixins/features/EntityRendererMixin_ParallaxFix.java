package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_ParallaxFix {
    //#if MC==10809
    @Shadow
    private Minecraft mc;

    @ModifyConstant(method = "orientCamera", constant = @Constant(floatValue = -0.1F))
    private float patcher$modifyParallax(float original) {
        return PatcherConfig.parallaxFix ? 0.05F : original;
    }

    @Redirect(method = "renderWorldDirections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"))
    private void patcher$moveDebugCursor(float x, float y, float z) {
        if (PatcherConfig.parallaxFix) {
            Vec3 vec = mc.getRenderViewEntity().getLook(1);
            GlStateManager.translate(vec.xCoord * 0.15F, (vec.yCoord * 0.15F) + y, vec.zCoord * 0.15F);
        } else {
            GlStateManager.translate(x, y, z);
        }
    }

    @Inject(method = "renderWorldDirections", at = @At("HEAD"), cancellable = true)
    private void patcher$disableDebugCursorInThirdPerson(CallbackInfo ci) {
        if (PatcherConfig.parallaxFix && mc.gameSettings.thirdPersonView > 0) {
            ci.cancel();
        }
    }
    //#endif
}
