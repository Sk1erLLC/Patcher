package club.sk1er.patcher.mixins.bugfixes.optifine;

//#if MC==10809
import net.minecraft.client.renderer.RegionRenderCache;
//#endif

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(
    //#if MC==10809
    RegionRenderCache.class
    //#else
    //$$ net.minecraft.client.Minecraft.class
    //#endif
)
public class RegionRenderCacheMixin_FixConnectedTextures {

    //#if MC==10809
    @Shadow
    @Final
    private static IBlockState DEFAULT_STATE;

    @Shadow
    private IBlockState[] blockStates;

    @Inject(
        method = "getBlockState",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RegionRenderCache;blockStates:[Lnet/minecraft/block/state/IBlockState;", ordinal = 0, shift = At.Shift.AFTER),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        cancellable = true
    )
    private void patcher$connectedTexturesBoundsCheck(BlockPos pos, CallbackInfoReturnable<IBlockState> cir, int positionIndex) {
        if (positionIndex < 0 || positionIndex >= this.blockStates.length) {
            cir.setReturnValue(DEFAULT_STATE);
        }
    }
    //#endif
}
