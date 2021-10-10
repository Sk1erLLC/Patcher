package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.particle.EffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin_ChangeParticleLimit {
    @ModifyConstant(method = "addEffect", constant = @Constant(intValue = 4000))
    private int patcher$changeMaxParticleLimit(int original) {
        return PatcherConfig.maxParticleLimit;
    }
}
