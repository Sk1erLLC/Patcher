package club.sk1er.patcher.hooks;

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

    private static float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3) {
        if (c == 0 && !t0) c = Math.max(0, Math.max(s1, s2) - 1);
        if (s1 == 0 && !t1) s1 = Math.max(0, c - 1);
        if (s2 == 0 && !t2) s2 = Math.max(0, c - 1);
        if (s3 == 0 && !t3) s3 = Math.max(0, Math.max(s1, s2) - 1);
        return (float) (c + s1 + s2 + s3) * 0x20 / (4 * 0xFFFF);
    }

    public static void updateLightMatrix(BlockPos blockPos, IBlockAccess world, Block block,
                                         boolean[][][] translucent, int[][][] skyBrightness, int[][][] blockBrightness,
                                         float[][][] ao, float[][][][] skyLight, float[][][][] blockLight) {
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    final BlockPos pos = blockPos.add(x - 1, y - 1, z - 1);
                    final Block blockState = world.getBlockState(pos).getBlock();
                    translucent[x][y][z] = !blockState.isVisuallyOpaque() || block.getLightOpacity() == 0;
                    final int brightness = blockState.getMixedBrightnessForBlock(world, pos);
                    skyBrightness[x][y][z] = (brightness >> 0x14) & 0xF;
                    blockBrightness[x][y][z] = (brightness >> 0x04) & 0xF;
                    ao[x][y][z] = blockState.getAmbientOcclusionLightValue();
                }
            }
        }

        for (EnumFacing side : sides) {
            if (!block.doesSideBlockRendering(world, blockPos, side)) {
                final int x = side.getFrontOffsetX() + 1;
                final int y = side.getFrontOffsetY() + 1;
                final int z = side.getFrontOffsetZ() + 1;
                skyBrightness[x][y][z] = Math.max(skyBrightness[1][1][1] - 1, skyBrightness[x][y][z]);
                blockBrightness[x][y][z] = Math.max(blockBrightness[1][1][1] - 1, blockBrightness[x][y][z]);
            }
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    final int x1 = x << 1;
                    final int y1 = y << 1;
                    final int z1 = z << 1;

                    final int sxyz = skyBrightness[x1][y1][z1];
                    final int bxyz = blockBrightness[x1][y1][z1];
                    final boolean txyz = translucent[x1][y1][z1];

                    final int sxz = skyBrightness[x1][1][z1], sxy = skyBrightness[x1][y1][1], syz = skyBrightness[1][y1][z1];
                    final int bxz = blockBrightness[x1][1][z1], bxy = blockBrightness[x1][y1][1], byz = blockBrightness[1][y1][z1];
                    final boolean txz = translucent[x1][1][z1], txy = translucent[x1][y1][1], tyz = translucent[1][y1][z1];

                    final int sx = skyBrightness[x1][1][1], sy = skyBrightness[1][y1][1], sz = skyBrightness[1][1][z1];
                    final int bx = blockBrightness[x1][1][1], by = blockBrightness[1][y1][1], bz = blockBrightness[1][1][z1];
                    final boolean tx = translucent[x1][1][1], ty = translucent[1][y1][1], tz = translucent[1][1][z1];

                    skyLight[0][x][y][z] = combine(sx, sxz, sxy, txz || txy ? sxyz : sx,
                        tx, txz, txy, txz || txy ? txyz : tx);
                    blockLight[0][x][y][z] = combine(bx, bxz, bxy, txz || txy ? bxyz : bx,
                        tx, txz, txy, txz || txy ? txyz : tx);

                    skyLight[1][x][y][z] = combine(sy, sxy, syz, txy || tyz ? sxyz : sy,
                        ty, txy, tyz, txy || tyz ? txyz : ty);
                    blockLight[1][x][y][z] = combine(by, bxy, byz, txy || tyz ? bxyz : by,
                        ty, txy, tyz, txy || tyz ? txyz : ty);

                    skyLight[2][x][y][z] = combine(sz, syz, sxz, tyz || txz ? sxyz : sz,
                        tz, tyz, txz, tyz || txz ? txyz : tz);
                    blockLight[2][x][y][z] = combine(bz, byz, bxz, tyz || txz ? bxyz : bz,
                        tz, tyz, txz, tyz || txz ? txyz : tz);
                }
            }
        }
    }

    public static void updateFlatLighting(Block block, IBlockAccess world, BlockPos blockPos) {
        full = block.isFullCube();
        packed[0] = block.getMixedBrightnessForBlock(world, blockPos);

        for (final EnumFacing side : sides) {
            final int i = side.ordinal() + 1;
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
