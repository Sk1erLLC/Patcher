package club.sk1er.patcher.ducks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public interface BlockExt {
    boolean patcher$doesSideBlockChestOpening(IBlockAccess world, BlockPos pos, EnumFacing side);
}
