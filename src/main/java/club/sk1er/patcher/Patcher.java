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

package club.sk1er.patcher;

import club.sk1er.patcher.command.DeleteNameHistoryCommand;
import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.config.PatcherSoundConfig;
import club.sk1er.patcher.coroutines.MCDispatchers;
import club.sk1er.patcher.hooks.MinecraftHook;
import club.sk1er.patcher.metrics.MetricsRenderer;
import club.sk1er.patcher.render.ScreenshotPreview;
import club.sk1er.patcher.screen.PatcherMenuEditor;
import club.sk1er.patcher.screen.render.DebugPerformanceRenderer;
import club.sk1er.patcher.screen.render.TitleFix;
import club.sk1er.patcher.screen.tab.MenuPreviewHandler;
import club.sk1er.patcher.tweaker.asm.C01PacketChatMessageTransformer;
import club.sk1er.patcher.tweaker.asm.GuiChatTransformer;
import club.sk1er.patcher.tweaker.launch.PatcherTweak;
import club.sk1er.patcher.util.armor.ArmorStatusRenderer;
import club.sk1er.patcher.util.chat.ChatHandler;
import club.sk1er.patcher.util.chat.ImagePreview;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.ReloadListener;
import club.sk1er.patcher.util.fov.FovHandler;
import club.sk1er.patcher.util.hotbar.HotbarItemsHandler;
import club.sk1er.patcher.util.keybind.KeybindChatPeek;
import club.sk1er.patcher.util.keybind.KeybindDropModifier;
import club.sk1er.patcher.util.keybind.FunctionKeyChanger;
import club.sk1er.patcher.util.keybind.KeybindNameHistory;
import club.sk1er.patcher.util.keybind.linux.LinuxKeybindFix;
import club.sk1er.patcher.util.screenshot.AsyncScreenshots;
import club.sk1er.patcher.util.sound.SoundHandler;
import club.sk1er.patcher.util.status.ProtocolDetector;
import club.sk1er.patcher.util.world.WorldHandler;
import club.sk1er.patcher.util.world.cloud.CloudHandler;
import club.sk1er.patcher.util.world.entity.EntityRendering;
import club.sk1er.patcher.util.world.entity.EntityTrace;
import club.sk1er.patcher.util.world.entity.culling.EntityCulling;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.modcore.api.ModCoreAPI;
import net.modcore.api.commands.Command;
import net.modcore.api.gui.Notifications;
import net.modcore.api.utils.Multithreading;
import net.modcore.api.utils.WebUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = Patcher.VERSION)
public class Patcher {

    @Mod.Instance("patcher")
    public static Patcher instance;

    // normal versions will be "1.x"
    // betas will be "1.x-beta-y" / "1.x-branch_beta-1"
    // rcs will be 1.x-rc-y
    // extra branches will be 1.x-branch-y
    public static final String VERSION = "1.6";

    private final Logger logger = LogManager.getLogger("Patcher");
    private final File logsDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "/" + File.separator + "logs" + File.separator);

    /**
     * Create a set of blacklisted servers, used for when a specific server doesn't allow for 1.8 clients to use
     * our 1.11 text length modifier (bring message length from 100 to 256, as done in 1.11 and above) {@link Patcher#addOrRemoveBlacklist(String)}.
     */
    private final Set<String> blacklistedServers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final File blacklistedServersFile = new File("./config/blacklisted_servers.txt");

    private final CloudHandler cloudHandler = new CloudHandler();
    private final DebugPerformanceRenderer debugPerformanceRenderer = new DebugPerformanceRenderer();

    private KeyBinding dropModifier;
    private KeyBinding nameHistory;
    private KeyBinding chatPeek;
    private KeyBinding hideScreen, debugView, clearShaders;

    private PatcherConfig patcherConfig;
    private PatcherSoundConfig patcherSoundConfig;

    private JsonObject duplicateModsJson;

    private boolean loadedGalacticFontRenderer;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        registerKeybinds(
            nameHistory = new KeybindNameHistory(), dropModifier = new KeybindDropModifier(),
            chatPeek = new KeybindChatPeek(), hideScreen = new FunctionKeyChanger.KeybindHideScreen(),
            debugView = new FunctionKeyChanger.KeybindDebugView(), clearShaders = new FunctionKeyChanger.KeybindClearShaders()
        );

        patcherConfig = new PatcherConfig();
        patcherConfig.preload();

        patcherSoundConfig = new PatcherSoundConfig();
        patcherSoundConfig.preload();

        SoundHandler target = new SoundHandler();
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        resourceManager.registerReloadListener(target);
        resourceManager.registerReloadListener(new ReloadListener());

        registerCommands(
            new PatcherCommand(),
            new AsyncScreenshots.FavoriteScreenshot(), new AsyncScreenshots.DeleteScreenshot(),
            new AsyncScreenshots.UploadScreenshot(), new AsyncScreenshots.CopyScreenshot(),
            new AsyncScreenshots.ScreenshotsFolder(), new DeleteNameHistoryCommand()
        );

        registerEvents(
            this, target, debugPerformanceRenderer, cloudHandler, dropModifier,
            new MenuPreviewHandler(), new EntityRendering(), new FovHandler(),
            new ChatHandler(), new HotbarItemsHandler(), new EntityCulling(),
            new ArmorStatusRenderer(), new EntityTrace(), new PatcherMenuEditor(),
            new ImagePreview(), new WorldHandler(), new TitleFix(), new LinuxKeybindFix(),
            new MetricsRenderer(), MinecraftHook.INSTANCE, ScreenshotPreview.INSTANCE
        );

        checkLogs();
        loadBlacklistedServers();
        fixSettings();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!loadedGalacticFontRenderer) {
            loadedGalacticFontRenderer = true;
            Minecraft.getMinecraft().standardGalacticFontRenderer.drawString("Force Load", 0, 0, 0);
        }

        MCDispatchers.INSTANCE.getIO().close();
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        final Notifications notifications = ModCoreAPI.getNotifications();
        final List<ModContainer> activeModList = Loader.instance().getActiveModList();
        for (ModContainer container : activeModList) {
            final String modId = container.getModId();
            final String modName = container.getName();
            if (PatcherConfig.entityCulling && modId.equals("enhancements")) {
                notifications.push(
                    "Patcher",
                    modName + " has been detected. Entity Culling is now disabled.");
                PatcherConfig.entityCulling = false;
            }

            if ((modId.equals("labymod") || modId.equals("enhancements")) && PatcherConfig.compactChat) {
                notifications.push(
                    "Patcher",
                    modName + " has been detected. Compact Chat is now disabled.");
                PatcherConfig.compactChat = false;
            }

            if (modId.equals("labymod")) {
                if (PatcherConfig.optimizedResourcePackDiscovery) {
                    notifications.push(
                        "Patcher",
                        "Labymod has been detected. Optimized Resource Pack Discovery is now disabled.");
                    PatcherConfig.optimizedResourcePackDiscovery = false;
                }

                if (PatcherConfig.chatPosition) {
                    notifications.push(
                        "Patcher",
                        "Labymod has been detected. Chat Position is now disabled.");
                    PatcherConfig.chatPosition = false;
                }
            }

            if (PatcherConfig.optimizedFontRenderer && modId.equals("smoothfont")) {
                notifications.push(
                    "Patcher",
                    "Patcher has identified Smooth Font and as such, Patcher's Font Renderer " +
                        "has been automatically disabled.\nRestart your game for Smooth Font to work again."
                );
                PatcherConfig.optimizedFontRenderer = false;
            }

            this.forceSaveConfig();
        }

        if (PatcherConfig.replacedModsWarning) {
            Multithreading.runAsync(() -> {
                try {
                    duplicateModsJson = new JsonParser().parse(WebUtil.fetchString("https://static.sk1er.club/patcher/duplicate_mods.json")).getAsJsonObject();
                } catch (Exception e) {
                    logger.error("Failed to fetch list of duplicate mods.", e);
                    return;
                }

                final Set<String> duplicates = new HashSet<>();
                for (ModContainer modContainer : activeModList) {
                    for (String modid : keySet(duplicateModsJson)) {
                        if (modContainer.getModId().contains(modid) && !duplicates.contains(modid)) {
                            duplicates.add(modContainer.getName());
                        }
                    }
                }

                if (!duplicates.isEmpty()) {
                    for (String duplicate : duplicates) {
                        notifications.push(
                            "Patcher",
                            "Patcher has identified the mod " + duplicate + " to be a duplicate." +
                                "\nThis message can be disabled in the Patcher settings."
                        );
                    }
                }
            });
        }

        final long time = (System.currentTimeMillis() - PatcherTweak.clientLoadTime) / 1000L;
        if (PatcherConfig.startupNotification) {
            notifications.push("Minecraft Startup", "Minecraft started in " + time + " seconds.");
        }

        logger.info("Minecraft started in {} seconds.", time);
    }

    /**
     * Runs when the user connects to a server.
     * Goes through the process of checking the current state of the server.
     * <p>
     * If the server is local, return and set the chat length to 256, as we modify the client to allow for
     * 256 message length in singleplayer through ASM in {@link C01PacketChatMessageTransformer#transform(ClassNode, String)}.
     * <p>
     * If the server is blacklisted, return and set the chat length to 100, as that server does not support 256 long
     * chat messages, and was manually blacklisted by the player.
     * <p>
     * If the server is not local nor blacklisted, check the servers protocol and see if it supports 315, aka 1.11.
     * If it does, then set the message length max to 256, otherwise return back to 100.
     *
     * @param event {@link FMLNetworkEvent.ClientConnectedToServerEvent}
     */
    @SubscribeEvent
    public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            GuiChatTransformer.maxChatLength = 256;
            return;
        }

        final String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (serverIP == null || blacklistedServers.contains(serverIP)) {
            GuiChatTransformer.maxChatLength = 100;
            return;
        }

        final CompletableFuture<Boolean> future = ProtocolDetector.instance.isCompatibleWithVersion(
            serverIP,
            315 // 1.11
        );

        Multithreading.runAsync(() -> {
            try {
                GuiChatTransformer.maxChatLength = future.get() ? 256 : 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EnhancementManager.getInstance().tick();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkLogs() {
        if (PatcherConfig.logOptimizer) {
            for (File file : Objects.requireNonNull(logsDirectory.listFiles())) {
                if (file.lastModified() <= (System.currentTimeMillis() - PatcherConfig.logOptimizerLength * 86400000L)) {
                    file.delete();
                }
            }
        }
    }

    private void registerKeybinds(KeyBinding... keybinds) {
        for (final KeyBinding keybind : keybinds) {
            ClientRegistry.registerKeyBinding(keybind);
        }
    }

    private void registerEvents(Object... events) {
        for (final Object event : events) {
            MinecraftForge.EVENT_BUS.register(event);
        }
    }

    private void registerCommands(Command... commands) {
        for (final Command command : commands) {
            ModCoreAPI.getCommandRegistry().registerCommand(command);
        }
    }

    private boolean isServerBlacklisted(String ip) {
        return ip != null && !ip.isEmpty() && !ip.trim().isEmpty() && blacklistedServers.contains(ip.trim());
    }

    public boolean addOrRemoveBlacklist(String input) {
        if (input == null || input.isEmpty() || input.trim().isEmpty()) {
            return false;
        } else {
            input = input.trim();

            if (isServerBlacklisted(input)) {
                blacklistedServers.remove(input);
                return false;
            } else {
                blacklistedServers.add(input);
                return true;
            }
        }
    }

    public void saveBlacklistedServers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(blacklistedServersFile))) {
            if (!blacklistedServersFile.getParentFile().exists() && !blacklistedServersFile.getParentFile().mkdirs()) {
                return;
            }

            if (!blacklistedServersFile.exists() && !blacklistedServersFile.createNewFile()) {
                return;
            }

            for (String server : blacklistedServers) {
                writer.write(server + System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("Failed to save blacklisted servers.", e);
        }
    }

    private void loadBlacklistedServers() {
        if (!blacklistedServersFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(blacklistedServersFile))) {
            String servers;

            while ((servers = reader.readLine()) != null) {
                blacklistedServers.add(servers);
            }
        } catch (IOException e) {
            logger.error("Failed to load blacklisted servers.", e);
        }
    }

    public Set<String> keySet(JsonObject json) throws NullPointerException {
        Set<String> keySet = new HashSet<>();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            keySet.add(entry.getKey());
        }

        return keySet;
    }

    private void fixSettings() {
        if (PatcherConfig.customZoomSensitivity > 1.0F) {
            PatcherConfig.customZoomSensitivity = 1.0F;
        }

        if (PatcherConfig.tabOpacity > 1.0F) {
            PatcherConfig.tabOpacity = 1.0F;
        }

        if (PatcherConfig.imagePreviewWidth > 1.0F) {
            PatcherConfig.imagePreviewWidth = 0.5F;
        }

        if (PatcherConfig.previewScale > 1.0F) {
            PatcherConfig.previewScale = 1.0F;
        }

        if (PatcherConfig.fireOverlayHeight < -0.5F || PatcherConfig.fireOverlayHeight > 1.5F) {
            PatcherConfig.fireOverlayHeight = 0.0F;
        }

        this.forceSaveConfig();
    }

    public PatcherConfig getPatcherConfig() {
        return patcherConfig;
    }

    public PatcherSoundConfig getPatcherSoundConfig() {
        return patcherSoundConfig;
    }

    public Logger getLogger() {
        return logger;
    }

    @SuppressWarnings("unused")
    public CloudHandler getCloudHandler() {
        return cloudHandler;
    }

    public KeyBinding getNameHistory() {
        return nameHistory;
    }

    public KeyBinding getChatPeek() {
        return chatPeek;
    }

    @SuppressWarnings("unused")
    public KeyBinding getDropModifier() {
        return dropModifier;
    }

    public DebugPerformanceRenderer getDebugPerformanceRenderer() {
        return debugPerformanceRenderer;
    }

    @SuppressWarnings("unused")
    public KeyBinding getHideScreen() {
        return hideScreen;
    }

    @SuppressWarnings("unused")
    public KeyBinding getDebugView() {
        return debugView;
    }

    @SuppressWarnings("unused")
    public KeyBinding getClearShaders() {
        return clearShaders;
    }

    public void forceSaveConfig() {
        this.patcherConfig.markDirty();
        this.patcherConfig.writeData();
    }
}
