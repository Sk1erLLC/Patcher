/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.hooks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class BlockPosHook {

    public static BlockPos offsetFast(BlockPos pos, EnumFacing facing) {
        switch (facing) {
            case UP:
                return new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            case DOWN:
                return new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

            case NORTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
            case SOUTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);

            case WEST:
                return new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            case EAST:
                return new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());

            default:
                return new BlockPos(pos.getX() + facing.getFrontOffsetX(), pos.getY() + facing.getFrontOffsetY(), pos.getZ() + facing.getFrontOffsetZ());
        }
    }

    public static BlockPos offsetFast(BlockPos pos, EnumFacing facing, int direction) {
        switch (facing) {
            case UP:
                return new BlockPos(pos.getX(), pos.getY() + direction, pos.getZ());
            case DOWN:
                return new BlockPos(pos.getX(), pos.getY() - direction, pos.getZ());

            case NORTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - direction);
            case SOUTH:
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + direction);

            case WEST:
                return new BlockPos(pos.getX() - direction, pos.getY(), pos.getZ());
            case EAST:
                return new BlockPos(pos.getX() + direction, pos.getY(), pos.getZ());

            default:
                return new BlockPos(pos.getX() + facing.getFrontOffsetX() * direction, pos.getY() + facing.getFrontOffsetY() * direction, pos.getZ() + facing.getFrontOffsetZ() * direction);
        }
    }
}
