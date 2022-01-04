package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderItemFrame.class)
public class RenderItemFrameMixin_NametagBackground {
    //#if MC==10809
    @ModifyArg(
        method = "renderName(Lnet/minecraft/entity/item/EntityItemFrame;DDD)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;color(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;"),
        index = 3
    )
    private float patcher$removeBackground(float alpha) {
        return PatcherConfig.disableNametagBoxes ? 0.0f : alpha;
    }
    //#endif
}
