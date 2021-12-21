package club.sk1er.patcher.mixins.features.levelhead;

import club.sk1er.patcher.hooks.NameTagRenderingHooks;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender")
public class AboveHeadRenderMixin_NametagShadow {

    @SuppressWarnings("DefaultAnnotationParam")
    @Dynamic("Levelhead")
    @Redirect(
        method = "render(Lnet/minecraft/client/gui/FontRenderer;Lclub/sk1er/mods/levelhead/display/LevelheadTag$LevelheadComponent;I)V", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true)
    )
    private int patcher$renderWithShadow(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return NameTagRenderingHooks.drawNametagText(fontRenderer, text, x, y, color);
    }
}
