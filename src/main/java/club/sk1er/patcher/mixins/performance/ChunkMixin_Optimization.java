package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.hooks.ChunkHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Chunk.class)
public class ChunkMixin_Optimization {
    @ModifyArg(
        method = "setBlockState",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V", ordinal = 0),
        index = 1
    )
    private int patcher$subtractOneFromY(int y) {
        return y - 1;
    }

    /**
     * @author LlamaLad7
     * @reason Optimization
     */
    @Overwrite
    public IBlockState getBlockState(BlockPos pos) {
        return ChunkHook.getBlockState((Chunk) (Object) this, pos);
    }
}
