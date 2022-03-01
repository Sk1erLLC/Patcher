package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.NameTagRenderingHooks;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderItemFrame.class)
public class RenderItemFrameMixin_NametagShadow {
    //#if MC==10809
    @Redirect(
        method = "renderName(Lnet/minecraft/entity/item/EntityItemFrame;DDD)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I")
    )
    private int patcher$renderWithShadow(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return NameTagRenderingHooks.drawNametagText(fontRenderer, text, x, y, color);
    }
    //#endif
}
