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

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.command.*;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
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
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = Patcher.VERSION)
public class Patcher {

    /**
     * Create an instance of Patcher to access methods without reinstating the main class.
     * This is never null, as {@link Mod.Instance} creates the instance.
     * <p>
     * Can crash if classloaded before properly loaded.
     */
    @Mod.Instance("patcher")
    public static Patcher instance;

    // normal versions will be "1.x"
    // betas will be "1.x+beta-y" / "1.x+branch_beta-1"
    // rcs will be 1.x+rc-y
    // extra branches will be 1.x+branch-y
    public static final String VERSION = "1.4+beta-2";

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
     * Create an instance of our config using {@link Vigilant}.
     */
    private PatcherConfig patcherConfig;

    /**
     * Create an instance of our sound config using {@link Vigilant}. The difference between this and the normal
     * config {@link Patcher#getPatcherConfig()} is that this is a much larger file, containing any sound possible,
     * and allowing for players to modify how loud a sound is (0 (mute)-2x).
     */
    private PatcherSoundConfig patcherSoundConfig;

    /**
     * Create an instance of our cloud handler, used in {@link RenderGlobal#renderClouds(float, int)}
     * through ASM, modified through {@link RenderGlobalTransformer}.
     */
    private CloudHandler cloudHandler;

    private DebugPerformanceRenderer debugPerformanceRenderer;

    private KeyBinding nameHistory;
    private KeyBinding dropModifier;
    private KeyBinding chatPeek;


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
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

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

        ClientCommandHandler.instance.registerCommand(new PatcherCommand());
        ClientCommandHandler.instance.registerCommand(new FovChangerCommand());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.FavoriteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.DeleteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.UploadScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.CopyScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.ScreenshotsFolder());
        ClientCommandHandler.instance.registerCommand(new SkinCacheRefresh());
        ClientCommandHandler.instance.registerCommand(new CoordsCommand());

        registerClass(this);
        registerClass(target);
        registerClass(new MenuPreviewHandler());
        registerClass(new EntityRendering());
        registerClass(new FovHandler());
        registerClass(new ChatHandler());
        registerClass(new HotbarItemsHandler());
        registerClass(new EntityCulling());
        registerClass(new ArmorStatusRenderer());
        registerClass(new EntityTrace());
        registerClass(new PatcherMenuEditor());
        registerClass(new ImagePreview());
        registerClass(new WorldHandler());
        registerClass(new TitleFix());
        registerClass(MinecraftHook.INSTANCE);
        registerClass(Viewer.getInstance());
        registerClass(debugPerformanceRenderer = new DebugPerformanceRenderer());
        registerClass(cloudHandler = new CloudHandler());

        checkLogs();
        loadBlacklistedServers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Close threads as it is no longer needed after startup.
        MCDispatchers.INSTANCE.getIO().close();
    }

    /**
     * Once the client has finished loading, alert the user of how long the client took to startup.
     *
     * @param event {@link FMLLoadCompleteEvent}
     */
    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        long time = (System.currentTimeMillis() - PatcherTweaker.clientLoadTime) / 1000L;

        if (PatcherConfig.startupNotification) {
            Notifications.INSTANCE.pushNotification(
                "Minecraft Startup", "Minecraft started in " + time + " seconds.");
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

        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (blacklistedServers.contains(serverIP)) {
            logger.info("Current server supports 1.11+, but doesn't allow for 1.8.9 to use a high chat length, setting to 100.");
            GuiChatTransformer.maxChatLength = 100;
            return;
        }

        CompletableFuture<Boolean> future = ProtocolDetector.instance.isCompatibleWithVersion(
            serverIP,
            315 // 1.11
        );

        Multithreading.runAsync(() -> {
            try {
                if (future.get()) {
                    logger.info("Server supports 1.11+, setting string length to 256.");
                    GuiChatTransformer.maxChatLength = 256;
                } else {
                    logger.info("Server doesn't support 1.11+, setting string length to 100.");
                    GuiChatTransformer.maxChatLength = 100;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        EnhancementManager.getInstance().tick();
    }

    /**
     * When the client is started, this is called.
     * The point of this is to check if the user has an option called "Log Optimizer" enabled,
     * and if they do, go through every file in the .minecraft/logs directory, and delete any
     * file that is older than the amount of days specified (1-90), and log what file was deleted.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkLogs() {
        if (PatcherConfig.logOptimizer) {
            for (File file : Objects.requireNonNull(logsDirectory.listFiles())) {
                if (file.lastModified() <= (System.currentTimeMillis() - PatcherConfig.logOptimizerLength * 86400000)) {
                    logger.info("Deleted log {}", file.getName());
                    file.delete();
                }
            }
        }
    }

    /**
     * Make life easier by calling this instead of calling {@link EventBus#register(Object)} manually so frequently.
     *
     * @param eventClass The class that should be registered.
     */
    private void registerClass(Object eventClass) {
        MinecraftForge.EVENT_BUS.register(eventClass);
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
        } catch (IOException ignored) {
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

    /**
     * Check if the current environment is development, or production.
     *
     * @return If the client is being ran in a development environment, return true, otherwise return false.
     */
    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
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
}
