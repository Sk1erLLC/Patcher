package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontRenderer.class)
public class FontRendererMixin_EmptyString {
    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true)
    private String patcher$replaceWithNullIfEmpty(String original) {
        // todo: optimize this
        while (original.startsWith('\u00a7' + "r")) {
            original = original.substring(2);
        }

        while (original.endsWith('\u00a7' + "r")) {
            original = original.substring(0, original.length() - 2);
        }

        if (original.isEmpty()) return null;
        return original;
    }
}
