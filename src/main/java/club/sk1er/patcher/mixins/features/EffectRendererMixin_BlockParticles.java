package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.particle.EffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin_BlockParticles {
    @Inject(method = {
        "addBlockDestroyEffects",
        "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V",
        "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/MovingObjectPosition;)V"},
        at = @At("HEAD"), cancellable = true)
    private void patcher$removeBlockBreakingParticles(CallbackInfo ci) {
        if (PatcherConfig.disableBlockBreakParticles) {
            ci.cancel();
        }
    }
}
