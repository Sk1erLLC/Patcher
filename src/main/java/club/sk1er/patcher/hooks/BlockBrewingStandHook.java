package club.sk1er.patcher.hooks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBrewingStandHook {
    public static void randomDisplayTick(World world, BlockPos pos, Random random) {
        final double x = (double) pos.getX() + 0.4F + random.nextDouble() * 0.2F;
        final double y = (double) pos.getY() + 0.7F + random.nextDouble() * 0.3F;
        final double z = (double) pos.getZ() + 0.4F + random.nextDouble() * 0.2F;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
