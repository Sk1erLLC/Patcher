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

import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.render.HistoryPopUp;
import club.sk1er.patcher.screen.ScreenHistory;
import club.sk1er.patcher.util.benchmark.AbstractBenchmark;
import club.sk1er.patcher.util.benchmark.BenchmarkResult;
import club.sk1er.patcher.util.benchmark.impl.ItemBenchmark;
import club.sk1er.patcher.util.benchmark.impl.TextBenchmark;
import club.sk1er.patcher.util.chat.ChatUtilities;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import club.sk1er.patcher.util.name.NameFetcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.modcore.api.commands.Command;
import net.modcore.api.commands.DefaultHandler;
import net.modcore.api.commands.DisplayName;
import net.modcore.api.commands.Greedy;
import net.modcore.api.commands.Options;
import net.modcore.api.commands.SubCommand;
import net.modcore.api.utils.GuiUtil;
import net.modcore.api.utils.Multithreading;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PatcherCommand extends Command {

    private final Map<String, AbstractBenchmark> benchmarkMap = new HashMap<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int randomBound = 85673;
    public static int randomChatMessageId;

    public PatcherCommand() {
        super("patcher");
        benchmarkMap.put("text", new TextBenchmark());
        benchmarkMap.put("item", new ItemBenchmark());
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(Objects.requireNonNull(Patcher.instance.getPatcherConfig().gui()));
    }

    @SubCommand(value = "resetcache", description = "Clears Font & Item cache. Typically should not be used.")
    public void resetCache() {
        EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class).invalidateAll();
        EnhancementManager.getInstance().getEnhancement(EnhancedItemRenderer.class).invalidateAll();
        ChatUtilities.sendNotification("Enhancement Cache", "&aCleared item & font enhancement cache.");
    }

    @SubCommand(value = "debugfps", description = "Made for debugging purposes. Typically should not be used.")
    public void debugFPS() {
        Patcher.instance.getDebugPerformanceRenderer().toggleFPS();
        ChatUtilities.sendNotification("Debug Renderer", "&aToggled the debug renderer.");
    }

    @SubCommand(value = "name", aliases = {"names", "namehistory"}, description = "Fetch someones past usernames.")
    public void names(@Nullable @DisplayName("name") String name) {
        final boolean invalidName = name == null || name.isEmpty();

        if (PatcherConfig.nameHistoryStyle == 0) {
            GuiUtil.open(name != null ? new ScreenHistory(name, false) : new ScreenHistory());
        } else if (PatcherConfig.nameHistoryStyle == 1) {
            if (invalidName) {
                ChatUtilities.sendNotification("Name History", "Username cannot be null.");
                return;
            }

            final NameFetcher nameFetcher = new NameFetcher();
            ChatUtilities.sendNotification("Name History", "Fetching usernames...");
            nameFetcher.execute(name);

            Multithreading.schedule(() -> {
                ChatComponentText message = new ChatComponentText(ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RESET + '\n');
                for (String usernames : nameFetcher.getNames()) {
                    message.appendText(ChatColor.GRAY + usernames + '\n');
                }
                message.appendText(ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "------------------------");

                final ChatComponentText deleteMessage = new ChatComponentText('\n' + ChatColor.YELLOW.toString() + ChatColor.BOLD + "Delete Message");
                final ChatStyle style = deleteMessage.getChatStyle();
                style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.GRAY + "This will only delete the most recent name history message.")));
                style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$deletenamehistory"));
                message.appendSibling(deleteMessage);

                randomChatMessageId = new Random().nextInt(randomBound);
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(message, randomChatMessageId);
                nameFetcher.getNames().clear();
            }, 2, TimeUnit.SECONDS);
        } else if (PatcherConfig.nameHistoryStyle == 2) {
            if (invalidName) {
                ChatUtilities.sendNotification("Name History", "Username cannot be null.");
                return;
            }

            HistoryPopUp.INSTANCE.addPopUp(name);
        }
    }

    @SubCommand(value = "blacklist", description = "Tell the client that you don't want to use the 1.11+ chat length on the specified server IP.")
    public void blacklist(@Greedy @DisplayName("ip") String ip) {
        final String status = Patcher.instance.addOrRemoveBlacklist(ip) ? "&cnow" : "&ano longer";
        ChatUtilities.sendNotification(
            "Server Blacklist",
            "Server &e\"" + ip + "\" &r is " + status + " &rblacklisted from chat length extension."
        );
        Patcher.instance.saveBlacklistedServers();
    }

    @SubCommand(value = "benchmark", description = "Made for debugging purposes. This should typically not be used.")
    public void benchmark(@Options({"all", "text", "item"}) String type, @Nullable @Greedy @DisplayName("extra") String extra) {
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

        //noinspection ConstantConditions
        runBenchmark(type, extra.split(" "), benchmark);
    }

    @SubCommand(value = "mode", description = "Made for debugging purposes. This should typically not be used.")
    public void mode(@Options({"vanilla", "optimized"}) String mode) {
        if (mode.equals("vanilla")) {
            toggleOptions(false);
            Patcher.instance.getDebugPerformanceRenderer().setMode("Vanilla");
            ChatUtilities.sendNotification("Debug Mode", "&aSet mode: &cVanilla&a.");
        } else {
            toggleOptions(true);
            Patcher.instance.getDebugPerformanceRenderer().setMode("Optimized");
            ChatUtilities.sendNotification("Debug Mode", "&aSet mode: &eOptimized&a.");
        }
    }

    @SubCommand(value = "fov", description = "Change your FOV to a custom value.")
    public void fov(@DisplayName("amount") float amount) {
        if (amount <= 0) {
            ChatUtilities.sendNotification("FOV Changer", "Changing your FOV to or below 0 is disabled due to game-breaking visual bugs.");
            return;
        } else if (amount > 110) {
            ChatUtilities.sendNotification("FOV Changer", "Changing your FOV above 110 is disabled due to game-breaking visual bugs.");
            return;
        }

        ChatUtilities.sendNotification(
            "FOV Changer",
            "FOV changed from &e" + mc.gameSettings.fovSetting + "&r to &a" + amount + "."
        );
        mc.gameSettings.fovSetting = amount;
        mc.gameSettings.saveOptions();
    }

    // todo: redo this and make it actually functional
    // currently skins reset once joining a new world
    @SubCommand(value = "refresh", aliases = "refreshskin", description = "Automatically refresh your skin without needing to relog.")
    public void refresh() {
        refreshSkin();
    }

    @SubCommand(value = "scale", aliases = {"invscale", "inventoryscale"}, description = "Change the scale of your inventory independant of your GUI scale.")
    public void scale(@Options({"help", "off", "none", "small", "normal", "large", "auto", "1", "2", "3", "5"}) String argument) {
        if (argument.equalsIgnoreCase("help")) {
            ChatUtilities.sendMessage("             &eInventory Scale", false);
            ChatUtilities.sendMessage("&7Usage: /inventoryscale <scaling>", false);
            ChatUtilities.sendMessage("&7Scaling may be a number between 1-5, or", false);
            ChatUtilities.sendMessage("&7small/normal/large/auto", false);
            ChatUtilities.sendMessage("&7Use '/inventoryscale off' to disable scaling.", false);
            return;
        }

        if (argument.equalsIgnoreCase("off") || argument.equalsIgnoreCase("none")) {
            ChatUtilities.sendNotification("Inventory Scale", "Disabled inventory scaling.");
            PatcherConfig.desiredScaleOverride = -1;
            Patcher.instance.forceSaveConfig();
            return;
        }

        int scaling;
        if (argument.equalsIgnoreCase("small")) {
            scaling = 1;
        } else if (argument.equalsIgnoreCase("normal")) {
            scaling = 2;
        } else if (argument.equalsIgnoreCase("large")) {
            scaling = 3;
        } else if (argument.equalsIgnoreCase("auto")) {
            scaling = 5;
        } else {
            try {
                scaling = Integer.parseInt(argument);
            } catch (Exception e) {
                ChatUtilities.sendNotification("Inventory Scale", "Invalid scaling identifier. Use '/patcher scale help' for assistance.");
                return;
            }
        }

        if (scaling < 1) {
            ChatUtilities.sendNotification("Inventory Scale", "Disabled inventory scaling.");
            PatcherConfig.desiredScaleOverride = -1;
            Patcher.instance.forceSaveConfig();
            return;
        } else if (scaling > 5) {
            ChatUtilities.sendNotification("Inventory Scale", "Invalid scaling. Must be between 1-5.");
            return;
        }

        ChatUtilities.sendNotification("Inventory Scale", "Set inventory scaling to " + scaling);
        PatcherConfig.desiredScaleOverride = scaling;
        Patcher.instance.forceSaveConfig();
    }

    @SubCommand(value = "sendcoords", description = "Send your current coordinates in chat.")
    public void sendCoords() {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.sendChatMessage("x: " + (int) player.posX + ", y: " + (int) player.posY + ", z: " + (int) player.posZ);
    }

    @SubCommand(value = "sounds", description = "Open the Sound Configuration GUI.")
    public void sounds() {
        GuiUtil.open(Objects.requireNonNull(Patcher.instance.getPatcherSoundConfig().gui()));
    }

    @SubCommand(value = "fps", description = "Choose what to limit the game's framerate to outside of Minecraft's options. 0 will use your normal framerate.")
    public void fps(@DisplayName("amount") int amount) {
        if (amount < 0) {
            ChatUtilities.sendNotification("Custom FPS Limiter", "You cannot set your framerate to a negative number.");
            return;
        }

        PatcherConfig.customFpsLimit = amount;
        Patcher.instance.forceSaveConfig();
        ChatUtilities.sendNotification("Custom FPS Limiter", "Custom framerate set to " + amount + ".");
    }

    /*@SubCommand("dev")
    public void dev() {
        if (!ClassTransformer.isDevelopment()) {
            ChatUtilities.sendNotification("Development", "This command is made only for development purposes, and should typically not even be loaded.");
            return;
        }

        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        for (int i = 0; i < 1000; i++) {
            player.addChatComponentMessage(new ChatComponentText("spam"));
        }
    }*/

    public static void refreshSkin() {
        try {
            final SkinManager skinManager = Minecraft.getMinecraft().getSkinManager();
            final GameProfile gameProfile = Minecraft.getMinecraft().getSession().getProfile();
            skinManager.loadProfileTextures(gameProfile, (type, location, profile) -> {
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    final NetworkPlayerInfo info = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(EntityPlayer.getUUID(gameProfile));
                    info.locationSkin = location;
                    info.skinType = profile.getMetadata("model");

                    if (info.skinType == null) {
                        info.skinType = "default";
                    }
                }
            }, true);

            ChatUtilities.sendNotification("Skin Cache", "Successfully refreshed skin cache.");
        } catch (Exception e) {
            ChatUtilities.sendNotification("Skin Cache", "Failed to refresh skin cache.");
            Patcher.instance.getLogger().error("Failed to refresh skin cache.", e);
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
        PatcherConfig.entityCulling = status;
        PatcherConfig.fullbright = status;
        PatcherConfig.lowAnimationTick = status;
        PatcherConfig.staticParticleColor = status;
        PatcherConfig.optimizedFontRenderer = status;
        PatcherConfig.cacheFontData = status;
        PatcherConfig.removeCloudTransparency = status;
        PatcherConfig.gpuCloudRenderer = status;
        PatcherConfig.optimizedItemRenderer = status;
        PatcherConfig.limitChunks = status;
        PatcherConfig.playerBackFaceCulling = status;
        PatcherConfig.entityBackFaceCulling = status;

        // fullbright requires a chunk reload once toggled, perform automatically
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }
}
