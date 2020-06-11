package club.sk1er.patcher.hooks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class FarmHook {
    private static final AxisAlignedBB[] AABB = {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D)
    };

    public static AxisAlignedBB getBox(World world, BlockPos pos, Block block) {
        final AxisAlignedBB axisAlignedBB = AABB[world.getBlockState(pos).getValue(BlockCrops.AGE)];

        final AxisAlignedBB adjusted = new AxisAlignedBB(
            pos.getX(), pos.getY(), pos.getZ(),
            pos.getX() + axisAlignedBB.maxX,
            pos.getY() + axisAlignedBB.maxY,
            pos.getZ() + axisAlignedBB.maxZ);

        block.maxY = axisAlignedBB.maxY;
        return adjusted;
    }
}
