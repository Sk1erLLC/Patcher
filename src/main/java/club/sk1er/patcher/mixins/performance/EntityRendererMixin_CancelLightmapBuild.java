package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_CancelLightmapBuild {

    @Unique
    private boolean patcher$createdLightmap;

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelLightmapBuild(CallbackInfo ci) {
        if (PatcherConfig.fullbright && this.patcher$createdLightmap) {
            ci.cancel();
        }
    }

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    private void patcher$setCreatedLightmap(CallbackInfo ci) {
        this.patcher$createdLightmap = true;
    }
}
