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

package club.sk1er.patcher.util.world.block;

import club.sk1er.patcher.asm.BakedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRendererDispatcherTransformer;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class BlockUtil {

    public static final AxisAlignedBB CACTUS_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    public static AxisAlignedBB offset(AxisAlignedBB bb, BlockPos pos) {
        return new AxisAlignedBB(bb.minX + (double) pos.getX(),
            bb.minY + (double) pos.getY(),
            bb.minZ + (double) pos.getZ(),
            bb.maxX + (double) pos.getX(),
            bb.maxY + (double) pos.getY(),
            bb.maxZ + (double) pos.getZ());
    }

    /**
     * Used in {@link BlockRendererDispatcherTransformer#transform(ClassNode, String)}
     */
    public static Set<Block> groundFoliageContainer = Sets.newHashSet(
        Blocks.tallgrass,
        Blocks.double_plant,
        Blocks.red_flower,
        Blocks.yellow_flower
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
