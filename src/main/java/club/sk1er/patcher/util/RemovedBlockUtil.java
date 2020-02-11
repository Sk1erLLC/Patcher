package club.sk1er.patcher.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Arrays;
import java.util.List;

public class RemovedBlockUtil {
    public static List<Block> grassContainer = Arrays.asList(
            Blocks.tallgrass,
            Blocks.double_plant
    );
}
