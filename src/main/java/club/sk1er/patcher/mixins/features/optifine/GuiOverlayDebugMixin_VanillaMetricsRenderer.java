package club.sk1er.patcher.mixins.features.optifine;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiOverlayDebug.class)
public abstract class GuiOverlayDebugMixin_VanillaMetricsRenderer {

    @Shadow
    protected abstract void renderLagometer();

    @Redirect(method = "renderDebugInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiOverlayDebug;renderLagometer()V"))
    private void patcher$useVanillaMetricsRenderer(GuiOverlayDebug instance) {
        if (!PatcherConfig.useVanillaMetricsRenderer) {
            this.renderLagometer();
        }
    }
}
