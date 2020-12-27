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

import net.modcore.api.ModCoreAPI;
import net.modcore.api.commands.CommandRegistry;
import net.modcore.api.gui.Notifications;
import net.modcore.api.utils.Multithreading;
import net.modcore.api.utils.WebUtil;
import club.sk1er.patcher.command.CoordsCommand;
import club.sk1er.patcher.command.FovChangerCommand;
import club.sk1er.patcher.command.InventoryScaleCommand;
import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.command.SkinCacheRefresh;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.config.PatcherSoundConfig;
import club.sk1er.patcher.coroutines.MCDispatchers;
import club.sk1er.patcher.hooks.MinecraftHook;
import club.sk1er.patcher.screen.PatcherMenuEditor;
import club.sk1er.patcher.screen.render.DebugPerformanceRenderer;
import club.sk1er.patcher.screen.render.TitleFix;
import club.sk1er.patcher.screen.tab.MenuPreviewHandler;
import club.sk1er.patcher.tweaker.PatcherTweaker;
import club.sk1er.patcher.tweaker.asm.C01PacketChatMessageTransformer;
import club.sk1er.patcher.tweaker.asm.GuiChatTransformer;
import club.sk1er.patcher.tweaker.asm.RenderGlobalTransformer;
import club.sk1er.patcher.util.armor.ArmorStatusRenderer;
import club.sk1er.patcher.util.chat.ChatHandler;
import club.sk1er.patcher.util.chat.ImagePreview;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.ReloadListener;
import club.sk1er.patcher.util.fov.FovHandler;
import club.sk1er.patcher.util.hotbar.HotbarItemsHandler;
import club.sk1er.patcher.util.keybind.KeybindChatPeek;
import club.sk1er.patcher.util.keybind.KeybindDropModifier;
import club.sk1er.patcher.util.keybind.KeybindNameHistory;
import club.sk1er.patcher.util.screenshot.AsyncScreenshots;
import club.sk1er.patcher.util.screenshot.viewer.Viewer;
import club.sk1er.patcher.util.sound.SoundHandler;
import club.sk1er.patcher.util.status.ProtocolDetector;
import club.sk1er.patcher.util.world.WorldHandler;
import club.sk1er.patcher.util.world.cloud.CloudHandler;
import club.sk1er.patcher.util.world.entity.EntityRendering;
import club.sk1er.patcher.util.world.entity.EntityTrace;
import club.sk1er.patcher.util.world.entity.culling.EntityCulling;
import club.sk1er.vigilance.Vigilant;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.koin.java.KoinJavaComponent;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = Patcher.VERSION)
public class Patcher extends DummyModContainer {

    // normal versions will be "1.x"
    // betas will be "1.x-beta-y" / "1.x-branch_beta-1"
    // rcs will be 1.x-rc-y
    // extra branches will be 1.x-branch-y
    public static final String VERSION = "1.5.1";

    /**
     * Create an instance of Patcher to access methods without reinstating the main class.
     * This is never null, as {@link Mod.Instance} creates the instance.
     * <p>
     * Can crash if classloaded before properly loaded.
     */
    @Mod.Instance("patcher")
    public static Patcher instance;

    private final Logger logger = LogManager.getLogger("Patcher");

    /**
     * Create a file link to the .minecraft/logs folder, used for {@link Patcher#checkLogs()}.
     */
    private final File logsDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "/" + File.separator + "logs" + File.separator);

    /**
     * Create a set of blacklisted servers, used for when a specific server doesn't allow for 1.8 clients to use
     * our 1.11 text length modifier (bring message length from 100 to 256, as done in 1.11 and above) {@link Patcher#addOrRemoveBlacklist(String)}.
     */
    private final Set<String> blacklistedServers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private final File blacklistedServersFile = new File("./config/blacklisted_servers.txt");

    /**
     * Create an instance of our cloud handler, used in {@link RenderGlobal#renderClouds(float, int)}
     * through ASM, modified through {@link RenderGlobalTransformer}.
     */
    private final CloudHandler cloudHandler = new CloudHandler();
    private final DebugPerformanceRenderer debugPerformanceRenderer = new DebugPerformanceRenderer();
    private final Viewer viewer = new Viewer();
    private KeyBinding dropModifier;
    private KeyBinding nameHistory;
    private KeyBinding chatPeek;

    /**
     * Create an instance of our config using {@link Vigilant}.
     */
    private PatcherConfig patcherConfig;

    /**
     * Create an instance of our sound config using {@link Vigilant}. The difference between this and the normal
     * config {@link Patcher#getPatcherConfig()} is that this is a much larger file, containing any sound possible,
     * and allowing for players to modify how loud a sound is (0 (mute)-2x).
     */
    private PatcherSoundConfig patcherSoundConfig;
    private JsonObject duplicateModsJson;
    private boolean loadedGalacticFontRenderer;

    public Patcher() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "patcher";
        meta.version = VERSION;
        meta.name = "Patcher";
        meta.authorList = Collections.singletonList("Sk1erLLC");
    }

    @Override
    public boolean registerBus(com.google.common.eventbus.EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    /**
     * Process important things that should be available by the time the game is done loading.
     * <p>
     * ModCore is initialized here, as well as any configuration.
     * Commands and other classes using events are also registered here.
     *
     * @param event {@link FMLInitializationEvent}
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        ClientRegistry.registerKeyBinding(nameHistory = new KeybindNameHistory());
        ClientRegistry.registerKeyBinding(dropModifier = new KeybindDropModifier());
        ClientRegistry.registerKeyBinding(chatPeek = new KeybindChatPeek());

        patcherConfig = new PatcherConfig();
        patcherConfig.preload();

        patcherSoundConfig = new PatcherSoundConfig();
        patcherSoundConfig.preload();

        SoundHandler target = new SoundHandler();
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        resourceManager.registerReloadListener(target);
        resourceManager.registerReloadListener(new ReloadListener());

        CommandRegistry commandRegistry = ModCoreAPI.getCommandRegistry();
        commandRegistry.registerCommand(new PatcherCommand());

        final ClientCommandHandler commandRegister = ClientCommandHandler.instance;
        commandRegister.registerCommand(new FovChangerCommand());
        commandRegister.registerCommand(new AsyncScreenshots.FavoriteScreenshot());
        commandRegister.registerCommand(new AsyncScreenshots.DeleteScreenshot());
        commandRegister.registerCommand(new AsyncScreenshots.UploadScreenshot());
        commandRegister.registerCommand(new AsyncScreenshots.CopyScreenshot());
        commandRegister.registerCommand(new AsyncScreenshots.ScreenshotsFolder());
        commandRegister.registerCommand(new SkinCacheRefresh());
        commandRegister.registerCommand(new CoordsCommand());
        commandRegister.registerCommand(new InventoryScaleCommand());

        this.registerEvents(
            this, target, viewer,
            debugPerformanceRenderer, cloudHandler, dropModifier,
            new MenuPreviewHandler(), new EntityRendering(), new FovHandler(),
            new ChatHandler(), new HotbarItemsHandler(), new EntityCulling(),
            new ArmorStatusRenderer(), new EntityTrace(), new PatcherMenuEditor(),
            new ImagePreview(), new WorldHandler(), new TitleFix(),
            MinecraftHook.INSTANCE
        );

        checkLogs();
        loadBlacklistedServers();
    }

    @Subscribe
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!loadedGalacticFontRenderer) {
            loadedGalacticFontRenderer = true;
            Minecraft.getMinecraft().standardGalacticFontRenderer.drawString("Force Load", 0, 0, 0);
        }

        // Close threads as it is no longer needed after startup.
        MCDispatchers.INSTANCE.getIO().close();
    }

    /**
     * Once the client has finished loading, alert the user of how long the client took to startup.
     *
     * @param event {@link FMLLoadCompleteEvent}
     */
    @Subscribe
    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        Notifications notifications = KoinJavaComponent.get(Notifications.class);

        final List<ModContainer> activeModList = Loader.instance().getActiveModList();
        for (ModContainer container : activeModList) {
            final String modId = container.getModId();
            final String modName = container.getName();
            if (PatcherConfig.entityCulling) {
                if (modId.equals("enhancements")) {
                    notifications.push(
                        "Patcher",
                        modName + " has been detected. Entity Culling is now disabled.\n" +
                            "This is an unfixable incompatibility without an update from the authors of " + modName);
                    PatcherConfig.entityCulling = false;
                }
            }

            if (modId.equals("labymod")) {
                if (PatcherConfig.compactChat) {
                    notifications.push(
                        "Patcher",
                        "Labymod has been detected. Compact Chat is now disabled.\n" +
                            "This is an unfixable incompatibility without an update from the authors of " + modName);
                    PatcherConfig.compactChat = false;
                }

                if (PatcherConfig.optimizedResourcePackDiscovery) {
                    notifications.push(
                        "Patcher",
                        "Labymod has been detected. Optimized Resource Pack Discovery is now disabled.\n" +
                            "This is an unfixable incompatibility without an update from the authors of " + modName);
                    PatcherConfig.optimizedResourcePackDiscovery = false;
                }
            }

            if (PatcherConfig.optimizedFontRenderer) {
                if (modId.equals("smoothfont")) {
                    notifications.push(
                        "Patcher",
                        "Patcher has identified Smooth Font and as such, Patcher's Optimized Font Renderer " +
                            "has been automatically disabled.\nRestart your game for Smooth Font to work again."
                    );
                    PatcherConfig.optimizedFontRenderer = false;
                }
            }

            this.forceSaveConfig();
        }

        if (PatcherConfig.replacedModsWarning) {
            Multithreading.runAsync(() -> {
                try {
                    duplicateModsJson = new JsonParser().parse(WebUtil.fetchString(
                        "https://static.sk1er.club/patcher/duplicate_mods.json")).getAsJsonObject();
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

        final long time = (System.currentTimeMillis() - PatcherTweaker.clientLoadTime) / 1000L;
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
            logger.info("User is in singleplayer, setting string length to 256.");
            GuiChatTransformer.maxChatLength = 256;
            return;
        }

        final String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (serverIP == null) {
            logger.info("Server IP is somehow null, returning.");
            return;
        }

        if (blacklistedServers.contains(serverIP)) {
            logger.info("Current server supports 1.11+, but doesn't allow for 1.8.9 to use a high chat length, setting to 100.");
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
        EnhancementManager.getInstance().tick();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkLogs() {
        if (PatcherConfig.logOptimizer) {
            for (File file : Objects.requireNonNull(logsDirectory.listFiles())) {
                if (file.lastModified() <= (System.currentTimeMillis() - PatcherConfig.logOptimizerLength * 86400000L)) {
                    logger.info("Deleted log {}", file.getName());
                    file.delete();
                }
            }
        }
    }

    /**
     * Make life easier by calling this instead of calling {@link EventBus#register(Object)} manually so frequently.
     *
     * @param events The class that should be registered.
     */
    private void registerEvents(Object... events) {
        for (final Object event : events) {
            MinecraftForge.EVENT_BUS.register(event);
        }
    }


    /**
     * Check if the current server they're connecting to is a blacklisted server.
     *
     * @param ip Current server IP.
     * @return If the IP is in {@link Patcher#blacklistedServers}, return true, otherwise return false.
     */
    private boolean isServerBlacklisted(String ip) {
        return ip != null && !ip.isEmpty() && !ip.trim().isEmpty() && blacklistedServers.contains(ip.trim());
    }

    /**
     * Used for adding or removing a server from the blacklist file and set.
     *
     * @param input Current server IP.
     * @return If the user's input is null or empty, then return false, cancelling the method. Otherwise, if the server
     * is inside of the blacklisted set, remove it from the blacklist and return false, else add it to the blacklist
     * and return true.
     */
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

    /**
     * Save the currently blacklisted servers to a text file, allowing the file to be read on server join.
     */
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
        //JsonObject#keySet not a thing in Minecraft 1.8's old AF GSON
        Set<String> keySet = new HashSet<>();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            keySet.add(entry.getKey());
        }

        return keySet;
    }

    /**
     * Allow for accessing super methods in the {@link PatcherConfig} class.
     *
     * @return The Patcher Config instance.
     */
    public PatcherConfig getPatcherConfig() {
        return patcherConfig;
    }

    /**
     * Allow for accessing super methods in the {@link PatcherSoundConfig} class.
     *
     * @return The Patcher Sound Config instance.
     */
    public PatcherSoundConfig getPatcherSoundConfig() {
        return patcherSoundConfig;
    }

    /**
     * Allow for accessing our logger outside of the class.
     *
     * @return The Patcher logger instance.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Allow for accessing our cloud handler outside of the class.
     * Suppressing as it is used in an ASM method {@link RenderGlobalTransformer}.
     *
     * @return The Cloud Handler instance.
     */
    @SuppressWarnings("unused")
    public CloudHandler getCloudHandler() {
        return cloudHandler;
    }

    /**
     * Allow for accessing our name history keybind outside of the class.
     *
     * @return The Name History keybind.
     */
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

    public Viewer getViewer() {
        return viewer;
    }

    public void forceSaveConfig() {
        this.patcherConfig.markDirty();
        this.patcherConfig.writeData();
    }
}
