package club.sk1er.patcher.test;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;
import java.util.List;

public class BytecodeOutput {

    public static boolean render;
    public static List<Block> grassBlocks = Arrays.asList(Blocks.tallgrass, Blocks.double_plant);

    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn) {
        if (grassBlocks.contains(state.getBlock()) && render) {
            return false;
        }

        System.out.println("H");
        return true;
    }
}
