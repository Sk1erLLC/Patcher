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

package club.sk1er.patcher.util.enhancement.benchmark.impl;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.enhancement.benchmark.AbstractBenchmark;
import club.sk1er.patcher.util.enhancement.benchmark.BenchmarkResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class ItemBenchmark extends AbstractBenchmark {

    private static final int renderItemIters = 100_000;

    @Override
    public BenchmarkResult[] benchmark(String[] args) {
        ItemCameraTransforms.TransformType[] types = ItemCameraTransforms.TransformType.values();

        BenchmarkResult[] results = new BenchmarkResult[types.length];

        for (int i = 0; i < types.length; ++i) {
            results[i] = renderItemBenchmark(types[i]);
        }

        return results;
    }

    private BenchmarkResult renderItemBenchmark(ItemCameraTransforms.TransformType type) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();

        long begin = System.nanoTime();

        for (int i = 0; i < renderItemIters; ++i) {
            renderItem.renderItem(item, type);
        }

        long end = System.nanoTime();
        return new BenchmarkResult(
            end - begin,
            renderItemIters,
            "Rendering " + renderItemIters + " of the item you are holding. (TransformType: " + type.name() + ")." + getItemResult()
        );
    }

    private String getItemResult() {
        return "\n&6Current Item Renderer: &3" + (PatcherConfig.optimizedItemRenderer ? "Optimized." : "Vanilla.");
    }
}
