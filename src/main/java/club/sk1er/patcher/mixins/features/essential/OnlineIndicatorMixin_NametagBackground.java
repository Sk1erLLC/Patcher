package club.sk1er.patcher.mixins.features.essential;

import club.sk1er.patcher.config.PatcherConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "gg.essential.handlers.OnlineIndicator")
public class OnlineIndicatorMixin_NametagBackground {

    @Dynamic("Essential")
    @ModifyArg(
        method = "drawNametagIndicator", remap = false,
        at = @At(value = "INVOKE", target = "Lgg/essential/universal/UGraphics;color(IIII)Lgg/essential/universal/UGraphics;"),
        index = 3
    )
    private static int patcher$removeBackground(int alpha) {
        return PatcherConfig.disableNametagBoxes ? 0 : alpha;
    }
}
