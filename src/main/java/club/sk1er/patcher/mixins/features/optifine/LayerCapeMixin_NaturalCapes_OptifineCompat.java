package club.sk1er.patcher.mixins.features.optifine;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LayerCape.class)
public class LayerCapeMixin_NaturalCapes_OptifineCompat {
    @Dynamic("Optifine")
    @ModifyConstant(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        constant = @Constant(floatValue = 165.0f)
    )
    public float patcher$disableOptifineSwingSidesClampConstant(float original) {
        if (PatcherConfig.naturalCapes) {
            return Float.MAX_VALUE;
        }
        return original;
    }

    @Dynamic("Optifine")
    @ModifyConstant(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        constant = @Constant(floatValue = -5.0f)
    )
    public float patcher$disableOptifineSwingClampConstant(float original) {
        if (PatcherConfig.naturalCapes) {
            return -Float.MAX_VALUE;
        }
        return original;
    }
}
