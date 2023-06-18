package club.sk1er.patcher.hooks;

import club.sk1er.patcher.mixins.accessors.BlockAccessor;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class CropUtilities {
    //#if MC==10809

    public static final AxisAlignedBB[] CARROT_POTATO_BOX = {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D)
    };

    public static final AxisAlignedBB[] WHEAT_BOX = {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
    };

    public static final AxisAlignedBB[] NETHER_WART_BOX = {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6875D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)
    };

    public static final AxisAlignedBB CACTUS_BOX = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);

    public static void updateCropsMaxY(World world, BlockPos pos, Block block) {
        final IBlockState blockState = world.getBlockState(pos);
        final Integer ageValue = blockState.getValue(BlockCrops.AGE);
        BlockAccessor accessor = (BlockAccessor) block;
        accessor.setMaxY(
            blockState.getBlock() instanceof BlockPotato || blockState.getBlock() instanceof BlockCarrot
                ? CARROT_POTATO_BOX[ageValue].maxY
                : WHEAT_BOX[ageValue].maxY
        );
    }

    public static void updateWartMaxY(World world, BlockPos pos, Block block) {
        ((BlockAccessor) block).setMaxY(
            NETHER_WART_BOX[world.getBlockState(pos).getValue(BlockNetherWart.AGE)].maxY
        );
    }

    public static void updateCactusBox(Block block) {
        block.setBlockBounds(
            (float) CACTUS_BOX.minX,
            (float) CACTUS_BOX.minY,
            (float) CACTUS_BOX.minZ,
            (float) CACTUS_BOX.maxX,
            (float) CACTUS_BOX.maxY,
            (float) CACTUS_BOX.maxZ
        );
    }
    //#endif
}
