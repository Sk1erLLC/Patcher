package club.sk1er.patcher.mixins;

import club.sk1er.patcher.hooks.FontRendererHook;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontRenderer.class)
public class FontRendererMixin_EmptyString {
    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true)
    private String patcher$replaceWithNullIfEmpty(String original) {
        if (original == null) return null;

        original = FontRendererHook.clearColorReset(original);
        if (original.isEmpty()) return null;
        return original;
    }
}
