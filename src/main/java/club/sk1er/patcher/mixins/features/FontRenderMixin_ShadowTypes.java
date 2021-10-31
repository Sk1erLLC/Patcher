package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class FontRenderMixin_ShadowTypes {

    @Inject(method = "renderString", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelDropShadow(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
        if (PatcherConfig.disableShadowedText && dropShadow) {
            cir.setReturnValue(0);
        }
    }

    @ModifyArg(
        method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I", ordinal = 0),
        index = 1
    )
    private float patcher$modifyXPosition(float x) {
        return PatcherConfig.alternateTextShadow ? x - 1 : x;
    }
}
