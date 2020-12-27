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
import club.sk1er.patcher.screen.ScreenHistory;
import club.sk1er.patcher.util.benchmark.AbstractBenchmark;
import club.sk1er.patcher.util.benchmark.BenchmarkResult;
import club.sk1er.patcher.util.benchmark.impl.ItemBenchmark;
import club.sk1er.patcher.util.benchmark.impl.TextBenchmark;
import club.sk1er.patcher.util.chat.ChatUtilities;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import net.minecraft.client.Minecraft;
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
        return "/" + getCommandName() + " [&amode <vanilla|optimized> &r| &bbenchmark <all, text, item> &r| &cdebugfps &r| &dsounds &r| &eresetcache &r| &2name [username] &r| &3blacklist <ip>&e]";
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
                        toggleOptions(false);
                        Patcher.instance.getDebugPerformanceRenderer().setMode("Vanilla");
                        ChatUtilities.sendNotification("Debug Renderer", "&aSet mode: &cVanilla&a.");
                        return;
                    }
                    case "optimized": {
                        toggleOptions(true);
                        Patcher.instance.getDebugPerformanceRenderer().setMode("Optimized");
                        ChatUtilities.sendNotification("Debug Renderer", "&aSet mode: &eOptimized&a.");
                        return;
                    }
                    default: {
                        ChatUtilities.sendNotification("Debug Renderer", "&cUnknown mode. Vanilla & Optimized are the only modes available.");
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
                    ChatUtilities.sendNotification("Performance Benchmark", "&3All of the benchmarks completed in " + seconds + "s.");
                    return;
                }

                final AbstractBenchmark benchmark = benchmarkMap.get(args[1]);

                if (benchmark == null) {
                    ChatUtilities.sendNotification("Performance Benchmark", "&cCan't find a \"" + args[1] + "\" benchmark by the name of \"" + args[1] + "\".");
                    return;
                }

                runBenchmark(args[1], Arrays.copyOfRange(args, 2, args.length), benchmark);
                return;
            } else if (args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("names")) {
                ModCore.getInstance().getGuiHandler().open(new ScreenHistory(args[1], false));
                return;
            } else if (args[0].equalsIgnoreCase("blacklist")) {
                final String status = Patcher.instance.addOrRemoveBlacklist(args[1]) ? "&cnow" : "&ano longer";
                ChatUtilities.sendNotification("Server Blacklist", "Server &e\"" + args[1] + "\" &r is " + status + " &rblacklisted from chat length extension.");
                Patcher.instance.saveBlacklistedServers();
                return;
            }

            return;
        } else if (args.length == 1) {
            switch (args[0]) {
                case "resetcache":
                    EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class).invalidateAll();
                    EnhancementManager.getInstance().getEnhancement(EnhancedItemRenderer.class).invalidateAll();
                    ChatUtilities.sendNotification("Enhancement Cache", "&aCleared item & font enhancement cache.");
                    return;

                case "debugfps":
                    Patcher.instance.getDebugPerformanceRenderer().toggleFPS();
                    ChatUtilities.sendNotification("Debug Renderer", "&aToggled the debug renderer.");
                    return;

                case "sounds":
                    ModCore.getInstance().getGuiHandler().open(Patcher.instance.getPatcherSoundConfig().gui());
                    return;

                case "name":
                case "names":
                    ModCore.getInstance().getGuiHandler().open(new ScreenHistory());
                    return;

                case "blacklist":
                    ChatUtilities.sendNotification("Server Blacklist", "&cPlease insert an IP to blacklist.");
                    return;

                case "benchmark":
                    ChatUtilities.sendNotification("Performance Benchmark", "&cPlease insert a test benchmark.\n&cAvailable options are: &eall, text, item&c.");
                    return;

                default:
                    ChatUtilities.sendMessage("&cInvalid command. Usage: &e" + getCommandUsage(sender) + "&c.");
                    return;
            }
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

    private void sendMessage(String message) {
        ChatUtilities.sendMessage(message, false);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
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
        PatcherConfig.optimizedResourcePackDiscovery = status;

        // fullbright requires a chunk reload once toggled, perform automatically
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }
}
