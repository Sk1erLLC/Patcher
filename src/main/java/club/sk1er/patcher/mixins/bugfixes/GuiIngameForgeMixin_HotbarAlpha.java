package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_HotbarAlpha {
    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void patcher$enableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.enableAlpha();
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void patcher$disableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.disableAlpha();
    }
}
