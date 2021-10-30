package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_ParallaxFix {

    @ModifyConstant(method = "orientCamera", constant = @Constant(floatValue = -0.1F))
    private float patcher$modifyParallax(float original) {
        return PatcherConfig.parallaxFix ? 0.05F : original;
    }
}
