package club.sk1er.patcher.mixins.features;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

//#if MC==11202
//$$ import club.sk1er.patcher.config.PatcherConfig;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_NametagBackground {
    //#if MC==11202
    //$$ @ModifyArg(
    //$$     method = "drawNameplate",
    //$$     at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;color(FFFF)Lnet/minecraft/client/renderer/BufferBuilder;"),
    //$$     index = 3
    //$$ )
    //$$ private static float patcher$removeBackground(float alpha) {
    //$$     return PatcherConfig.disableNametagBoxes ? 0.0f : alpha;
    //$$ }
    //#endif
}
