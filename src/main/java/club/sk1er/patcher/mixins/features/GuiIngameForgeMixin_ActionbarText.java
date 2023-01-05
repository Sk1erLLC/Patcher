package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.GuiIngameForgeHook;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_ActionbarText {

    @Shadow(remap = false)
    public static int left_height;

    @Redirect(
        method = "renderRecordOverlay",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"),
        remap = false
    )
    private int patcher$drawCustomActionbarText(FontRenderer instance, String text, int x, int y, int color) {
        return GuiIngameForgeHook.drawActionbarText(text, color);
    }

    @ModifyArg(
        method = "renderRecordOverlay",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"),
        index = 1,
        remap = false
    )
    private float patcher$fixOverlappingActionbarText(float y) {
        return PatcherConfig.fixActionbarOverlap && 68 < left_height ? y + 68f - left_height : y;
    }

}
