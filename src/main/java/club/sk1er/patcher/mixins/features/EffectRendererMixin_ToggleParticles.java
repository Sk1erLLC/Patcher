package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherParticleConfig;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin_ToggleParticles {
    @Inject(method = "spawnEffectParticle(IDDDDDD[I)Lnet/minecraft/client/particle/EntityFX;", at = @At("HEAD"), cancellable = true)
    private void cancelRendering(int id, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int[] parameters, CallbackInfoReturnable<EntityFX> cir) {
        PatcherParticleConfig config = Patcher.instance.getPatcherParticleConfig();

        if (config.map.getOrDefault(id, false)) {
            cir.cancel();
        }
    }
}
