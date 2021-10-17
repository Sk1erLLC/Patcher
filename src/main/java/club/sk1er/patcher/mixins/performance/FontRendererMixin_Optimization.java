package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.ducks.FontRendererExt;
import club.sk1er.patcher.hooks.FontRendererHook;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontRenderer.class)
public class FontRendererMixin_Optimization implements FontRendererExt {

    @Unique
    private final FontRendererHook patcher$fontRendererHook = new FontRendererHook((FontRenderer) (Object) this);

    /**
     * @author asbyth
     * @reason Use a string width cache
     */
    @Overwrite
    public int getStringWidth(String text) {
        return this.patcher$fontRendererHook.getStringWidth(text);
    }

    @Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
    private void patcher$useOptimizedRendering(String text, boolean shadow, CallbackInfo ci) {
        if (this.patcher$fontRendererHook.renderStringAtPos(text, shadow)) {
            ci.cancel();
        }
    }

    @Override
    public FontRendererHook patcher$getFontRendererHook() {
        return patcher$fontRendererHook;
    }
}
