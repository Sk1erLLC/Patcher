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

import net.modcore.api.commands.*;
import net.modcore.api.utils.GuiUtil;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.screen.ScreenHistory;
import club.sk1er.patcher.util.benchmark.AbstractBenchmark;
import club.sk1er.patcher.util.benchmark.BenchmarkResult;
import club.sk1er.patcher.util.benchmark.impl.ItemBenchmark;
import club.sk1er.patcher.util.benchmark.impl.TextBenchmark;
import club.sk1er.patcher.util.chat.ChatUtilities;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import jline.internal.Nullable;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class PatcherCommand extends Command {

    private final Map<String, AbstractBenchmark> benchmarkMap = new HashMap<>();

    public PatcherCommand() {
        super("patcher");
        benchmarkMap.put("text", new TextBenchmark());
        benchmarkMap.put("item", new ItemBenchmark());

        // [&amode <vanilla|optimized> &r| &bbenchmark <all, text, item> &r| &cdebugfps &r| &dsounds &r| &eresetcache &r| &2name [username] &r| &3blacklist <ip>&e]";
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(Patcher.instance.getPatcherConfig().gui());
    }

    @SubCommand("resetcache")
    public void resetCache() {
        EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class).invalidateAll();
        EnhancementManager.getInstance().getEnhancement(EnhancedItemRenderer.class).invalidateAll();
        ChatUtilities.sendNotification("Enhancement Cache", "&aCleared item & font enhancement cache.");
    }

    @SubCommand("debugfps")
    public void debugFPS() {
        Patcher.instance.getDebugPerformanceRenderer().toggleFPS();
        ChatUtilities.sendNotification("Debug Renderer", "&aToggled the debug renderer.");
    }

    @SubCommand(value = "names", aliases = { "name" })
    public void names(@Nullable @DisplayName("name") String name) {
        GuiUtil.open(name != null ? new ScreenHistory(name, false) : new ScreenHistory());
    }

    @SubCommand("blacklist")
    public void blacklist(@DisplayName("ip") String ip) {
        final String status = Patcher.instance.addOrRemoveBlacklist(ip) ? "&cnow" : "&ano longer";
        ChatUtilities.sendNotification(
            "Server Blacklist",
            "Server &e\"" + ip + "\" &r is " + status + " &rblacklisted from chat length extension."
        );
        Patcher.instance.saveBlacklistedServers();
    }

    @SubCommand("benchmark")
    public void benchmark(@Options({ "all", "text", "item" }) String type, @Nullable @Greedy @DisplayName("extra") String extra) {
        if (type.equals("all")) {
            long totalMillis = 0;

            for (Map.Entry<String, AbstractBenchmark> benchmarkEntry : benchmarkMap.entrySet()) {
                long millis = runBenchmark(benchmarkEntry.getKey(), new String[0], benchmarkEntry.getValue());
                totalMillis += millis;
            }

            float seconds = totalMillis / 1000F;
            ChatUtilities.sendNotification(
                "Performance Benchmark",
                "&3All of the benchmarks completed in " + seconds + "s."
            );
            return;
        }

        final AbstractBenchmark benchmark = benchmarkMap.get(type);

        if (benchmark == null) {
            ChatUtilities.sendNotification(
                "Performance Benchmark",
                "&cCan't find a benchmark by the name of \"" + type + "\"."
            );
            return;
        }

        runBenchmark(type, extra.split(" "), benchmark);
    }

    @SubCommand("mode")
    public void mode(@Options({ "vanilla", "optimized" }) String mode) {
        if (mode.equals("vanilla")) {
            toggleOptions(false);
            Patcher.instance.getDebugPerformanceRenderer().setMode("Vanilla");
            ChatUtilities.sendNotification("Debug Renderer", "&aSet mode: &cVanilla&a.");
        } else {
            toggleOptions(true);
            Patcher.instance.getDebugPerformanceRenderer().setMode("Optimized");
            ChatUtilities.sendNotification("Debug Renderer", "&aSet mode: &eOptimized&a.");
        }
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

    private void sendMessage(String message) {
        ChatUtilities.sendMessage(message, false);
    }

    private void toggleOptions(boolean status) {
        PatcherConfig.cullParticles = status;
        PatcherConfig.entityCulling = status;
        PatcherConfig.searchingOptimizationFix = status;
        PatcherConfig.fullbright = status;
        PatcherConfig.disableConstantFogColorChecking = status;
        PatcherConfig.lowAnimationTick = status;
        PatcherConfig.staticParticleColor = status;
        PatcherConfig.optimizedFontRenderer = status;
        PatcherConfig.cacheFontData = status;
        PatcherConfig.removeCloudTransparency = status;
        PatcherConfig.gpuCloudRenderer = status;
        PatcherConfig.glErrorChecking = status;
        PatcherConfig.optimizedItemRenderer = status;
        PatcherConfig.limitChunks = status;

        // fullbright requires a chunk reload once toggled, perform automatically
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }
}
