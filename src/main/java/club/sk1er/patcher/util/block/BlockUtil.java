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

import club.sk1er.patcher.tweaker.asm.BakedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRendererDispatcherTransformer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Blocks;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class BlockUtil {

    /**
     * Used in {@link BlockRendererDispatcherTransformer#transform(ClassNode, String)}
     */
    public static List<Block> grassContainer = Arrays.asList(
        Blocks.tallgrass,
        Blocks.double_plant
    );

    /**
     * Used in {@link BakedQuadTransformer#transform(ClassNode, String)}
     */
    public static boolean bakedQuadEquals(BakedQuad one, Object other) {
        if (!(other instanceof BakedQuad)) {
            return false;
        }

        BakedQuad two = (BakedQuad) other;

        if (one == two) return true;
        if (one.hasTintIndex() != two.hasTintIndex()) return false;
        if (one.getFace() != two.getFace()) return false;
        return one.getVertexData() == two.getVertexData();
    }
}
