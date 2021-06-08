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

import club.sk1er.patcher.config.PatcherConfig;
import gg.essential.api.EssentialAPI;
import gg.essential.api.utils.MinecraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
//import org.koin.java.KoinJavaComponent;

@SuppressWarnings("unused")
public class FarmHook {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final MinecraftUtils minecraftUtils = EssentialAPI.getMinecraftUtil();//KoinJavaComponent.get(MinecraftUtils.class);

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

    public static void updateCropsMaxY(World world, BlockPos pos, Block block) {
        final IBlockState blockState = world.getBlockState(pos);
        final Integer ageValue = blockState.getValue(BlockCrops.AGE);

        if (PatcherConfig.futureHitBoxes && (minecraftUtils.isHypixel() || mc.isIntegratedServerRunning())) {
            block.maxY = blockState.getBlock() instanceof BlockPotato || blockState.getBlock() instanceof BlockCarrot
                ? CARROT_POTATO_BOX[ageValue].maxY
                : WHEAT_BOX[ageValue].maxY;
            return;
        }

        block.maxY = 0.25F;
    }

    public static void updateWartMaxY(World world, BlockPos pos, Block block) {
        block.maxY = PatcherConfig.futureHitBoxes && (minecraftUtils.isHypixel() || mc.isIntegratedServerRunning())
            ? NETHER_WART_BOX[world.getBlockState(pos).getValue(BlockNetherWart.AGE)].maxY
            : .25F;
    }
}
