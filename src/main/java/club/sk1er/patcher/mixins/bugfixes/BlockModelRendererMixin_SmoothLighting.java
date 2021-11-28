package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC==11202
//$$ import net.minecraft.block.state.IBlockState;
//#endif

@Mixin(targets = "net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")
public class BlockModelRendererMixin_SmoothLighting {
    @Redirect(
        //#if MC==10809
        method = "updateVertexBrightness(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;[FLjava/util/BitSet;)V",
        //#else
        //$$ method = "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;[FLjava/util/BitSet;)V",
        //#endif
        at = @At(
            value = "INVOKE",
            target =
                //#if MC==10809
                "Lnet/minecraft/block/Block;isTranslucent()Z"
                //#else
                //$$ "Lnet/minecraft/block/Block;isTranslucent(Lnet/minecraft/block/state/IBlockState;)Z"
                //#endif
        )
    )
    private boolean patcher$betterSmoothLighting(Block block) {
        //#if MC==10809
        return !block.isVisuallyOpaque() || block.getLightOpacity() == 0;
        //#else
        //$$ IBlockState state = block.getDefaultState();
        //$$ return !block.causesSuffocation(state) || block.getLightOpacity(state) == 0;
        //#endif
    }
}
