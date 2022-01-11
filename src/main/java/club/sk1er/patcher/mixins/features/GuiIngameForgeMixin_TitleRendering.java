package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import gg.essential.universal.UResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_TitleRendering extends GuiIngame {

    @Shadow
    private FontRenderer fontrenderer;

    public GuiIngameForgeMixin_TitleRendering(Minecraft mc) {
        super(mc);
    }

    @Inject(method = "renderTitle", at = @At("HEAD"),cancellable = true, remap = false)
    private void patcher$cancelTitleRender(CallbackInfo ci) {
        if (PatcherConfig.disableTitles) ci.cancel();
    }

    @ModifyArgs(method = "renderTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void patcher$scaleTitle(Args args) {
        float titleScale = PatcherConfig.titleScale;
        if (PatcherConfig.autoTitleScale) {
            float longestWidth = Math.max(fontrenderer.getStringWidth(displayedTitle) * 4.0F * titleScale, fontrenderer.getStringWidth(displayedSubTitle) * 2.0F * titleScale);
            if (longestWidth > UResolution.getScaledWidth()) {
                titleScale = (UResolution.getScaledWidth() / longestWidth) * PatcherConfig.titleScale;
            }
        }
        args.set(0, ((float) args.get(0)) * titleScale);
        args.set(1, ((float) args.get(1)) * titleScale);
        args.set(2, ((float) args.get(2)) * titleScale);
    }
}
