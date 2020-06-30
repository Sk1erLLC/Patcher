package club.sk1er.patcher.util.world.entity.culling;

import net.minecraft.util.BlockPos;

public class BetterBlockPos extends BlockPos {

    private int x, y, z;

    public BetterBlockPos() { //Used by EntityTransformer
        this(0, 0, 0);
    }

    public BetterBlockPos(int x, int y, int z) {
        super(x, y, z);
        this.x = x;
        this.z = z;
        this.y = y;
    }

    public void update(int x, int y, int z) {
        this.x = x;
        this.z = z;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }
}
