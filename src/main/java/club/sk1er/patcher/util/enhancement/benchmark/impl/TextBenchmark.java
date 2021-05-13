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
import net.minecraft.client.gui.FontRenderer;

public class TextBenchmark extends AbstractBenchmark {

    private static final int sepIters = 100_000;
    private static final int genericIters = 10_000;

    private String longString;
    private FontRenderer fontRenderer;

    @Override
    public void setup() {
        super.setup();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&o&");

        for (int i = 0; i < 100_000; ++i) {
            stringBuilder.append("e");
        }

        longString = stringBuilder.toString();
        fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    }

    @Override
    public void tearDown() {
        super.tearDown();

        longString = null;
        fontRenderer = null;
    }

    @Override
    public BenchmarkResult[] benchmark(String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "list":
                    sendMessage("&3Options are: &6separate, single, average, all.");
                    return new BenchmarkResult[]{};
                case "separate":
                    return new BenchmarkResult[]{separateRendersBenchmark()};
                case "single":
                    return new BenchmarkResult[]{singleRenderBenchmark()};
                case "average":
                    return new BenchmarkResult[]{averageTextBenchmark()};
                case "all":
                    return new BenchmarkResult[]{separateRendersBenchmark(), singleRenderBenchmark(),
                        averageTextBenchmark()};
                default:
                    sendMessage("Can't find a text benchmark by the name of " + args[0]);
                    return new BenchmarkResult[]{};
            }
        }

        return new BenchmarkResult[]{separateRendersBenchmark(), singleRenderBenchmark(), averageTextBenchmark()};
    }

    private BenchmarkResult averageTextBenchmark() {
        String average = "&c&mThis is my sentence that contains lots of standard characters";

        long begin = System.nanoTime();

        for (int i = 0; i < genericIters; ++i) {
            fontRenderer.drawStringWithShadow(average, 0, 0, -1);
        }

        long end = System.nanoTime();
        return new BenchmarkResult(
            end - begin,
            genericIters,
            "Rendering the string: \"" + average + "&r&6\" " + genericIters + " times." + getFontResult()
        );
    }

    private BenchmarkResult singleRenderBenchmark() {
        long begin = System.nanoTime();

        fontRenderer.drawStringWithShadow(longString, 0, 0, -1);

        long end = System.nanoTime();

        return new BenchmarkResult(
            end - begin,
            1,
            "Rendering " + (longString.length() - 4) + " letters, all at once." + getFontResult()
        );
    }

    private BenchmarkResult separateRendersBenchmark() {
        long begin = System.nanoTime();

        for (int i = 0; i < sepIters; ++i) {
            fontRenderer.drawStringWithShadow("&o&me", 0, 0, -1);
        }

        long end = System.nanoTime();
        return new BenchmarkResult(
            end - begin,
            sepIters,
            "Rendering " + sepIters + " letters, one after each other." + getFontResult()
        );
    }

    private String getFontResult() {
        return "\n&6Current Font Renderer: &3" + (PatcherConfig.optimizedFontRenderer ? "Optimized." : "Vanilla.");
    }
}
