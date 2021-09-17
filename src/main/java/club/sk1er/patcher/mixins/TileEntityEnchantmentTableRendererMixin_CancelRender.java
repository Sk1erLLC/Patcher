package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityEnchantmentTableRenderer.class)
public class TileEntityEnchantmentTableRendererMixin_CancelRender {

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(CallbackInfo ci) {
        if (PatcherConfig.disableEnchantmentBooks) {
            ci.cancel();
        }
    }
}
