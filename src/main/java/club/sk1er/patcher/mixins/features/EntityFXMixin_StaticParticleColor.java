package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFX.class)
public class EntityFXMixin_StaticParticleColor {
    @Redirect(method = "renderParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EntityFX;getBrightnessForRender(F)I"))
    private int patcher$staticParticleColor(EntityFX entityFX, float partialTicks) {
        return PatcherConfig.staticParticleColor ? 15728880 : entityFX.getBrightnessForRender(partialTicks);
    }
}
