package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.IParticleFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EffectRenderer.class)
public interface EffectRendererAccessor {
    @Accessor
    Map<Integer, IParticleFactory> getParticleTypes();
}
