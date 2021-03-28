package club.sk1er.patcher.hooks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

@SuppressWarnings("unused")
public class ChunkHook {

    public static IBlockState getBlockState(Chunk chunk, BlockPos pos) {
        final int y = pos.getY();

        if (y >= 0 && y >> 4 < chunk.getBlockStorageArray().length) {
            final ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y >> 4];
            if (storage != null) return storage.get(pos.getX() & 15, y & 15, pos.getZ() & 15);
        }

        return Blocks.air.getDefaultState();
    }
}
