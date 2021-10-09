package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.GuiIngameForgeHook;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_ActionbarText {
    @Redirect(
        method = "renderRecordOverlay",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I")
    )
    private int patcher$drawCustomActionbarText(FontRenderer instance, String text, int x, int y, int color) {
        GuiIngameForgeHook.drawActionbarText(text, color);
        return 0;
    }
}
