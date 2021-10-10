package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.particle.EffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin_BlockParticles {
    @Inject(
        method = {
            "addBlockDestroyEffects",
            "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V"
        }, at = @At("HEAD"), cancellable = true
    )
    private void patcher$removeBlockBreakingParticles(CallbackInfo ci) {
        if (PatcherConfig.disableBlockBreakParticles) {
            ci.cancel();
        }
    }

    // this is added by forge, so this shouldn't be remapped (and causes a compile error if it is)
    @Inject(
        method = "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/MovingObjectPosition;)V",
        at = @At("HEAD"), cancellable = true, remap = false
    )
    private void patcher$removeBlockBreakingParticles_Forge(CallbackInfo ci) {
        if (PatcherConfig.disableBlockBreakParticles) {
            ci.cancel();
        }
    }
}
