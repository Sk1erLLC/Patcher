package club.sk1er.patcher.mixins.features.essential;

import club.sk1er.patcher.config.PatcherConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "gg.essential.handlers.OnlineIndicator")
public class OnlineIndicatorMixin_NametagBackground {

    @Dynamic("Essential")
    @Inject(
        method = "getTextBackgroundOpacity", remap = false,
        at = @At("HEAD"),
        cancellable = true
    )
    private static void patcher$removeBackground(CallbackInfoReturnable<Integer> cir) {
        if (PatcherConfig.disableNametagBoxes) {
            cir.setReturnValue(0);
        }
    }
}
