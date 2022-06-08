package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.ducks.FontRendererExt;
import club.sk1er.patcher.hooks.FontRendererHook;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = FontRenderer.class, priority = 1100)
public class FontRendererMixin_Optimization implements FontRendererExt {

    @Unique
    private final FontRendererHook patcher$fontRendererHook = new FontRendererHook((FontRenderer) (Object) this);

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    public void getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if (PatcherConfig.optimizedFontRenderer) {
            cir.setReturnValue(this.patcher$fontRendererHook.getStringWidth(text));
        } else {
            Map<String, Integer> cache = this.patcher$fontRendererHook.getEnhancedFontRenderer().getStringWidthCache();
            if (cache.size() != 0) {
                cache.clear();
            }
        }
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
