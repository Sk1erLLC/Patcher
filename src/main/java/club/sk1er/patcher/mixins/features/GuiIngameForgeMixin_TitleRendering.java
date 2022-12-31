package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import cc.polyfrost.oneconfig.libs.universal.UResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngameForge.class, remap = false)
public class GuiIngameForgeMixin_TitleRendering extends GuiIngame {

    @Shadow
    private FontRenderer fontrenderer;

    public GuiIngameForgeMixin_TitleRendering(Minecraft mc) {
        super(mc);
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelTitleRender(CallbackInfo ci) {
        if (PatcherConfig.disableTitles) ci.cancel();
    }

    @Inject(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", ordinal = 0, shift = At.Shift.AFTER, remap = true))
    private void patcher$modifyTitle(int l, int age, float opacity, CallbackInfo ci) {
        float titleScale = PatcherConfig.titleScale;
        if (PatcherConfig.autoTitleScale) {
            float width = fontrenderer.getStringWidth(displayedTitle) * 4.0F;
            if (width > UResolution.getScaledWidth()) {
                titleScale = (UResolution.getScaledWidth() / width) * PatcherConfig.titleScale;
            }
        }
        GlStateManager.scale(titleScale, titleScale, titleScale);
    }
    @Inject(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", ordinal = 1, shift = At.Shift.AFTER, remap = true))
    private void patcher$modifySubtitle(int l, int age, float opacity, CallbackInfo ci) {
        float titleScale = PatcherConfig.titleScale;
        if (PatcherConfig.autoTitleScale) {
            float width = fontrenderer.getStringWidth(displayedSubTitle) * 2.0F;
            if (width > UResolution.getScaledWidth()) {
                titleScale = (UResolution.getScaledWidth() / width) * PatcherConfig.titleScale;
            }
        }
        GlStateManager.scale(titleScale, titleScale, titleScale);
    }

    @ModifyConstant(method = "renderTitle", constant = @Constant(intValue = 255))
    private int patcher$modifyOpacity(int constant) {
        return (int) (PatcherConfig.titleOpacity * 255);
    }

    @ModifyConstant(method = "renderTitle", constant = @Constant(floatValue = 255.0F))
    private float patcher$modifyOpacity(float constant) {
        return PatcherConfig.titleOpacity * 255;
    }
}
