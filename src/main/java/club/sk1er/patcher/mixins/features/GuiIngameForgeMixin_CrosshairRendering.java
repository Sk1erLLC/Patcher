package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_CrosshairRendering {
    @Redirect(
        method = "renderCrosshairs",
        slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=775", ordinal = 0)),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;tryBlendFuncSeparate(IIII)V", ordinal = 0)
    )
    private void patcher$handleCrosshairInvert(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (!PatcherConfig.removeInvertFromCrosshair) {
            GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }
}
