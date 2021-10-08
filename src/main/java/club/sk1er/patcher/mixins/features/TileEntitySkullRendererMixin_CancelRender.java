package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntitySkullRenderer.class)
public class TileEntitySkullRendererMixin_CancelRender {
    @Inject(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySkull;DDDFI)V", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(CallbackInfo ci) {
        if (PatcherConfig.disableSkulls) {
            ci.cancel();
        }
    }
}
