package club.sk1er.patcher.mixins.invscale;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.screen.ResolutionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(GuiScreen.class)
public class GuiScreenMixin_InventoryScale {

    @ModifyVariable(method = "setWorldAndResolution", at = @At("HEAD"), ordinal = 0)
    private int patcher$modifyScreenWidth(int width) {
        if (Minecraft.getMinecraft().thePlayer != null && ((Object) this) instanceof GuiContainer) {
            int desiredScale = PatcherConfig.desiredScaleOverride;
            ResolutionHelper.setCurrentScaleOverride(desiredScale);
            ResolutionHelper.setScaleOverride(desiredScale);
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            ResolutionHelper.setScaleOverride(-1);
            return resolution.getScaledWidth();
        }

        return width;
    }

    @ModifyVariable(method = "setWorldAndResolution", at = @At("HEAD"), ordinal = 1)
    private int patcher$modifyScreenHeight(int height) {
        if (Minecraft.getMinecraft().thePlayer != null && ((Object) this) instanceof GuiContainer) {
            int desiredScale = PatcherConfig.desiredScaleOverride;
            ResolutionHelper.setCurrentScaleOverride(desiredScale);
            ResolutionHelper.setScaleOverride(desiredScale);
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            ResolutionHelper.setScaleOverride(-1);
            return resolution.getScaledHeight();
        }

        return height;
    }

    @Inject(method = "handleInput", at = @At("HEAD"))
    private void patcher$handleModifiedInputHead(CallbackInfo ci) {
        if (Minecraft.getMinecraft().thePlayer != null && ((Object) this) instanceof GuiContainer) {
            ResolutionHelper.setScaleOverride(ResolutionHelper.getCurrentScaleOverride());
        }
    }

    @Inject(method = "handleInput", at = @At("TAIL"))
    private void patcher$handleModifiedInputTail(CallbackInfo ci) {
        ResolutionHelper.setScaleOverride(-1);
    }
}
