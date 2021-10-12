package club.sk1er.patcher.mixins.performance;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin_ReduceAllocations extends Vec3i {

    public BlockPosMixin_ReduceAllocations(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos up() {
        return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos up(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX(), this.getY() + offset, this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos down() {
        return new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos down(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX(), this.getY() - offset, this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos north() {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos north(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX(), this.getY(), this.getZ() - offset);
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos south() {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos south(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX(), this.getY(), this.getZ() + offset);
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos west() {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos west(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX() - offset, this.getY(), this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos east() {
        return new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos east(int offset) {
        return offset == 0 ? (BlockPos) (Object) this : new BlockPos(this.getX() + offset, this.getY(), this.getZ());
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos offset(EnumFacing direction) {
        switch (direction) {
            case UP:
                return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
            case DOWN:
                return new BlockPos(this.getX(), this.getY() - 1, this.getZ());

            case NORTH:
                return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
            case SOUTH:
                return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);

            case WEST:
                return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
            case EAST:
                return new BlockPos(this.getX() + 1, this.getY(), this.getZ());

            default:
                return new BlockPos(
                    this.getX() + direction.getFrontOffsetX(),
                    this.getY() + direction.getFrontOffsetY(),
                    this.getZ() + direction.getFrontOffsetZ()
                );
        }
    }

    /**
     * @author asbyth
     * @reason Inline method to reduce allocations
     */
    @Overwrite
    public BlockPos offset(EnumFacing direction, int offset) {
        switch (direction) {
            case UP:
                return new BlockPos(this.getX(), this.getY() + offset, this.getZ());
            case DOWN:
                return new BlockPos(this.getX(), this.getY() - offset, this.getZ());

            case NORTH:
                return new BlockPos(this.getX(), this.getY(), this.getZ() - offset);
            case SOUTH:
                return new BlockPos(this.getX(), this.getY(), this.getZ() + offset);

            case WEST:
                return new BlockPos(this.getX() - offset, this.getY(), this.getZ());
            case EAST:
                return new BlockPos(this.getX() + offset, this.getY(), this.getZ());

            default:
                return new BlockPos(
                    this.getX() + direction.getFrontOffsetX() * offset,
                    this.getY() + direction.getFrontOffsetY() * offset,
                    this.getZ() + direction.getFrontOffsetZ() * offset
                );
        }
    }
}
