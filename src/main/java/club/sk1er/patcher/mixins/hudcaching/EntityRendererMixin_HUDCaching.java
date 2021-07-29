package club.sk1er.patcher.mixins.hudcaching;

import club.sk1er.patcher.screen.render.caching.HUDCaching;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRenderer.class, priority = 1001)
public class EntityRendererMixin_HUDCaching {
    @Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void patcher$renderCachedHUD(GuiIngame guiIngame, float partialTicks) {
        HUDCaching.renderCachedHud((EntityRenderer) (Object) this, guiIngame, partialTicks);
    }
}
