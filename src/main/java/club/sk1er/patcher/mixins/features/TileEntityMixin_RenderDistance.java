package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntity.class)
public class TileEntityMixin_RenderDistance {
    @Inject(method = "getMaxRenderDistanceSquared", at = @At("HEAD"), cancellable = true)
    private void patcher$renderDistance(CallbackInfoReturnable<Double> cir) {
        if (PatcherConfig.entityRenderDistanceToggle) {
            cir.setReturnValue(Math.pow(Math.min(PatcherConfig.tileEntityRenderDistance, PatcherConfig.entityRenderDistance), 2));
        }
    }
}
