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

package club.sk1er.patcher.command;

import club.sk1er.mods.core.ModCore;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.benchmark.AbstractBenchmark;
import club.sk1er.patcher.util.benchmark.BenchmarkResult;
import club.sk1er.patcher.util.benchmark.impl.ItemBenchmark;
import club.sk1er.patcher.util.benchmark.impl.TextBenchmark;
import club.sk1er.patcher.util.chat.ChatUtilities;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PatcherCommand extends CommandBase {

    private final Map<String, AbstractBenchmark> benchmarkMap = new HashMap<>();

    public PatcherCommand() {
        benchmarkMap.put("text", new TextBenchmark());
        benchmarkMap.put("item", new ItemBenchmark());
    }

    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "patcher";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender user
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + "[mode <vanilla|optimized>|benchmark <all, text, item>|debugfps|sounds|resetcache]";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender user
     * @param args   arguments
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("mode")) {
                switch (args[1]) {
                    case "vanilla": {
                        PatcherConfig.cullParticles = false;
                        PatcherConfig.entityCulling = false;
                        PatcherConfig.searchingOptimizationFix = false;
                        PatcherConfig.fullbright = false;
                        PatcherConfig.disableConstantFogColorChecking = false;
                        PatcherConfig.lowAnimationTick = false;
                        PatcherConfig.staticParticleColor = false;
                        PatcherConfig.optimizedFontRenderer = false;
                        PatcherConfig.cacheFontData = false;
                        PatcherConfig.removeCloudTransparency = false;
                        PatcherConfig.gpuCloudRenderer = false;
                        PatcherConfig.glErrorChecking = false;
                        PatcherConfig.optimizedItemRenderer = false;
                        Patcher.instance.getImagePreview().setMode("Vanilla");
                        ChatUtilities.sendMessage("Set mode: Vanilla");
                        return;
                    }
                    case "optimized": {
                        PatcherConfig.cullParticles = true;
                        PatcherConfig.entityCulling = true;
                        PatcherConfig.searchingOptimizationFix = true;
                        PatcherConfig.fullbright = true;
                        PatcherConfig.disableConstantFogColorChecking = true;
                        PatcherConfig.lowAnimationTick = true;
                        PatcherConfig.staticParticleColor = true;
                        PatcherConfig.optimizedFontRenderer = true;
                        PatcherConfig.cacheFontData = true;
                        PatcherConfig.removeCloudTransparency = true;
                        PatcherConfig.gpuCloudRenderer = true;
                        PatcherConfig.glErrorChecking = true;
                        PatcherConfig.optimizedItemRenderer = true;
                        Patcher.instance.getImagePreview().setMode("Optimized");
                        ChatUtilities.sendMessage("Set mode: Optimized");
                        return;
                    }
                    default: {
                        ChatUtilities.sendMessage("Unknown mode");
                        return;
                    }
                }
            } else if (args[0].equalsIgnoreCase("benchmark") || args[0].equalsIgnoreCase("bench")) {
                if (args[1].equals("all")) {
                    long totalMillis = 0;

                    for (Map.Entry<String, AbstractBenchmark> benchmarkEntry : benchmarkMap.entrySet()) {
                        long millis = runBenchmark(benchmarkEntry.getKey(), new String[0], benchmarkEntry.getValue());
                        totalMillis += millis;
                    }

                    float seconds = totalMillis / 1000F;
                    sendMessage("&3All of the benchmarks completed in " + seconds + "s.");
                    return;
                }

                AbstractBenchmark benchmark = benchmarkMap.get(args[1]);

                if (benchmark == null) {
                    sendMessage("&cCan't find a \"" + args[1] + "\" benchmark by the name of \"" + args[1] + "\".");
                    return;
                }

                runBenchmark(args[1], Arrays.copyOfRange(args, 2, args.length), benchmark);
            }

            return;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("resetcache")) {
            EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class).invalidateAll();
            EnhancementManager.getInstance().getEnhancement(EnhancedItemRenderer.class).invalidateAll();
            sendMessage("Cleared Enhancement cache.");
            return;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("debugfps")) {
            Patcher.instance.getImagePreview().toggleFPS();
            sendMessage("Toggled");
            return;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("sounds")) {
            ModCore.getInstance().getGuiHandler().open(Patcher.instance.getPatcherSoundConfig().gui());
            return;
        }

        ModCore.getInstance().getGuiHandler().open(Patcher.instance.getPatcherConfig().gui());
    }

    private long runBenchmark(String benchmarkName, String[] args, AbstractBenchmark benchmark) {
        sendMessage("&3Beginning benchmark with the name " + benchmarkName + ".");

        benchmark.setup();
        benchmark.warmUp();
        BenchmarkResult[] results = benchmark.benchmark(args);
        benchmark.tearDown();

        long totalMillis = 0;

        for (BenchmarkResult result : results) {
            sendMessage("&9&m--------------------------------------");
            sendMessage("&6" + result.getName());

            float millis = result.getDeltaTime() / (float) 1_000_000;
            float nanosPerIteration = result.getDeltaTime() / (float) result.getIterations();

            sendMessage("&6Benchmark took a total time of " + millis + "ms.");
            sendMessage("&6Each iteration took an average of " + nanosPerIteration + "ns.");

            sendMessage("&9&m--------------------------------------");

            totalMillis += millis;
        }

        sendMessage("&3Completed the " + benchmarkName + " benchmark.");
        return totalMillis;
    }

    private void sendMessage(String text) {
        ChatUtilities.sendMessage(text, false);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    public Map<String, AbstractBenchmark> getBenchmarkMap() {
        return benchmarkMap;
    }
}
