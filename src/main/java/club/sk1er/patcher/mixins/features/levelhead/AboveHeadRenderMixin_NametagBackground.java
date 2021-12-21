package club.sk1er.patcher.mixins.features.levelhead;

import club.sk1er.patcher.config.PatcherConfig;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender")
public class AboveHeadRenderMixin_NametagBackground {

    @SuppressWarnings("DefaultAnnotationParam")
    @Dynamic("Levelhead")
    @ModifyArg(
        method = "renderName", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;color(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;", remap = true),
        index = 3
    )
    private float patcher$removeBackground(float alpha) {
        return PatcherConfig.disableNametagBoxes ? 0.0f : alpha;
    }
}
