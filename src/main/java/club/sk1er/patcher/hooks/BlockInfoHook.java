package club.sk1er.patcher.hooks;

//#if MC==10809
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.BlockInfo;

@SuppressWarnings("unused")
public class BlockInfoHook {

    private static final EnumFacing[] sides = EnumFacing.values();
    private static final int[] packed = new int[7];
    private static boolean full;

    public static void updateFlatLighting(Block block, IBlockAccess world, BlockPos blockPos) {
        full = block.isFullCube();
        packed[0] = block.getMixedBrightnessForBlock(world, blockPos);

        for (EnumFacing side : sides) {
            int i = side.ordinal() + 1;
            packed[i] = block.getMixedBrightnessForBlock(world, blockPos.offset(side));
        }
    }

    public static void updateLightmap(BlockInfo blockInfo, float[] normal, float[] lightmap, float x, float y, float z) {
        final float e1 = 0.99f;
        final float e2 = 0.95f;

        EnumFacing side = null;

        if ((full || y < -e1) && normal[1] < -e2) side = EnumFacing.DOWN;
        else if ((full || y > e1) && normal[1] > e2) side = EnumFacing.UP;
        else if ((full || z < -e1) && normal[2] < -e2) side = EnumFacing.NORTH;
        else if ((full || z > e1) && normal[2] > e2) side = EnumFacing.SOUTH;
        else if ((full || x < -e1) && normal[0] < -e2) side = EnumFacing.WEST;
        else if ((full || x > e1) && normal[0] > e2) side = EnumFacing.EAST;

        final int i = side == null ? 0 : side.ordinal() + 1;
        final int brightness = packed[i];

        lightmap[0] = ((float) ((brightness >> 0x04) & 0xF) * 0x20) / 0xFFFF;
        lightmap[1] = ((float) ((brightness >> 0x14) & 0xF) * 0x20) / 0xFFFF;
    }
}
//#endif
