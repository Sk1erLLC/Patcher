package club.sk1er.patcher.mixins.performance.hudcaching;

import club.sk1er.patcher.screen.render.caching.HUDCaching;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin_HUDCaching {
    private static boolean blendEnabled;

    @Inject(method = "blendFunc(II)V", at = @At("HEAD"), cancellable = true)
    private static void patcher$blendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        if (HUDCaching.renderingCacheOverride) {
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

    @Inject(method = "tryBlendFuncSeparate(IIII)V", at = @At("HEAD"), cancellable = true)
    private static void patcher$tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (HUDCaching.renderingCacheOverride && dstFactorAlpha != GL11.GL_ONE_MINUS_SRC_ALPHA) {
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

    @Inject(method = "disableBlend", at = @At("HEAD"))
    private static void patcher$disableBlend(CallbackInfo ci) {
        if (HUDCaching.renderingCacheOverride) {
            blendEnabled = false;
        }
    }

    @Inject(method = "enableBlend", at = @At("HEAD"))
    private static void patcher$enableBlend(CallbackInfo ci) {
        if (HUDCaching.renderingCacheOverride) {
            blendEnabled = true;
        }
    }

    @Inject(method = "color(FFFF)V", at = @At("HEAD"), cancellable = true)
    private static void patcher$color(float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (!blendEnabled && HUDCaching.renderingCacheOverride && alpha < 1f) {
            GlStateManager.color(red, green, blue, 1f);
            ci.cancel();
        }
    }
}
