package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin_SkipProgress {
    @Inject(method = "setLoadingProgress", at = @At("HEAD"), cancellable = true)
    private void patcher$skipProgress(int progress, CallbackInfo ci) {
        if (progress < 0 || PatcherConfig.optimizedWorldSwapping) {
            ci.cancel();
        }
    }
}
