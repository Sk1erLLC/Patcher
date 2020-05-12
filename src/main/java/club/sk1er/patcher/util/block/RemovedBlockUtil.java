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

package club.sk1er.patcher.util.block;

import club.sk1er.patcher.tweaker.asm.BlockRendererDispatcherTransformer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;

/**
 * Used in {@link BlockRendererDispatcherTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class RemovedBlockUtil {
    public static List<Block> grassContainer = Arrays.asList(
        Blocks.tallgrass,
        Blocks.double_plant
    );
}
