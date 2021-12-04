package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.NameTagRenderingHooks;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_ShadowedNametags {
    //#if MC==11202
    //$$ @Redirect(method = "drawNameplate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    //$$ private static int patcher$useShadowedNametagRendering(FontRenderer fontRenderer, String text, int x, int y, int color) {
    //$$      return NameTagRenderingHooks.drawNametagText(fontRenderer, text, x, y, color);
    //$$ }
    //#endif
}
