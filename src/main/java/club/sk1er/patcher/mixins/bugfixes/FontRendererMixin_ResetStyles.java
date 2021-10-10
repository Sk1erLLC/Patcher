package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin_ResetStyles {

    @Shadow protected abstract void resetStyles();

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I",
        at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I",
            ordinal = 0, shift = At.Shift.AFTER
        )
    )
    private void patcher$resetStyle(CallbackInfo ci) {
        this.resetStyles();
    }
}
