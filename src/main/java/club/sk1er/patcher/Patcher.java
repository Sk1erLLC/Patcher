package club.sk1er.patcher;

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.command.BlacklistServerCommand;
import club.sk1er.patcher.command.CoordsCommand;
import club.sk1er.patcher.command.FovChangerCommand;
import club.sk1er.patcher.command.NameHistoryCommand;
import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.command.PatcherSoundsCommand;
import club.sk1er.patcher.command.SkinCacheRefresh;
import club.sk1er.patcher.command.WireframeClouds;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.config.PatcherSoundConfig;
import club.sk1er.patcher.hooks.MinecraftHook;
import club.sk1er.patcher.sound.SoundHandler;
import club.sk1er.patcher.status.ProtocolDetector;
import club.sk1er.patcher.tab.TabToggleHandler;
import club.sk1er.patcher.tweaker.PatcherTweaker;
import club.sk1er.patcher.tweaker.asm.GuiChatTransformer;
import club.sk1er.patcher.util.armor.ArmorStatusRenderer;
import club.sk1er.patcher.util.chat.ChatHandler;
import club.sk1er.patcher.util.cloud.CloudHandler;
import club.sk1er.patcher.util.culling.EntityCulling;
import club.sk1er.patcher.util.entity.EntityRendering;
import club.sk1er.patcher.util.entity.EntityTrace;
import club.sk1er.patcher.util.fov.FovHandler;
import club.sk1er.patcher.util.hotbar.HotbarItemsHandler;
import club.sk1er.patcher.util.keybind.KeybindDropStack;
import club.sk1er.patcher.util.keybind.KeybindHandler;
import club.sk1er.patcher.util.keybind.KeybindNameHistory;
import club.sk1er.patcher.util.screen.PatcherMenuEditor;
import club.sk1er.patcher.util.screenshot.AsyncScreenshots;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = "1.2")
public class Patcher {

    @Mod.Instance("patcher")
    public static Patcher instance;
    private static boolean cacheDevelopment;
    private final Logger LOGGER = LogManager.getLogger("Patcher");
    private final File logsDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "/" + File.separator + "logs" + File.separator);
    private final Set<String> blacklistedServers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private PatcherConfig patcherConfig;
    private PatcherSoundConfig patcherSoundConfig;
    private CloudHandler cloudHandler;
    private KeyBinding nameHistory;
    private KeyBinding dropKeybind;

    public static boolean isDevelopment() {
        if (cacheDevelopment) {
            return true;
        } else {
            Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
            if (o == null) return false;
            return cacheDevelopment = (boolean) o;
        }
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ClientRegistry.registerKeyBinding(nameHistory = new KeybindNameHistory());
        ClientRegistry.registerKeyBinding(dropKeybind = new KeybindDropStack());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        patcherConfig = new PatcherConfig();
        patcherConfig.preload();

        patcherSoundConfig = new PatcherSoundConfig();
        patcherSoundConfig.preload();

        SoundHandler target = new SoundHandler();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(target);

        ClientCommandHandler.instance.registerCommand(new PatcherCommand());
        ClientCommandHandler.instance.registerCommand(new PatcherSoundsCommand());
        ClientCommandHandler.instance.registerCommand(new FovChangerCommand()); // ve replacement
        ClientCommandHandler.instance.registerCommand(new NameHistoryCommand());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.FavoriteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.DeleteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.UploadScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.CopyScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.ScreenshotsFolder());
        ClientCommandHandler.instance.registerCommand(new BlacklistServerCommand());
        ClientCommandHandler.instance.registerCommand(new SkinCacheRefresh());
        ClientCommandHandler.instance.registerCommand(new CoordsCommand());

        if (isDevelopment()) {
            ClientCommandHandler.instance.registerCommand(new WireframeClouds());
        }

        registerClass(this);
        registerClass(target);
        registerClass(new TabToggleHandler());
        registerClass(new EntityRendering());
        registerClass(new FovHandler());
        registerClass(new ChatHandler());
        registerClass(new HotbarItemsHandler());
        registerClass(MinecraftHook.INSTANCE);
        registerClass(new EntityCulling());
        registerClass(new ArmorStatusRenderer());
        registerClass(new EntityTrace());
        registerClass(new PatcherMenuEditor());
        registerClass(cloudHandler = new CloudHandler());
        registerClass(new KeybindHandler());

        checkLogs();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkLogs() {
        if (PatcherConfig.logOptimizer) {
            for (File file : Objects.requireNonNull(logsDirectory.listFiles())) {
                if (file.lastModified() <= (System.currentTimeMillis() - PatcherConfig.logOptimizerLength * 86400000)) {
                    LOGGER.info("Deleted log {}", file.getName());
                    file.delete();
                }
            }
        }
    }

    private void registerClass(Object eventClass) {
        MinecraftForge.EVENT_BUS.register(eventClass);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        float time = (System.currentTimeMillis() - PatcherTweaker.clientLoadTime) / 1000f;

        if (PatcherConfig.startupNotification) {
            Notifications.INSTANCE.pushNotification(
                "Minecraft Startup", "Minecraft started in " + time + " seconds.");
        }

        LOGGER.info("Minecraft started in {} seconds.", time);
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
        File blacklistedServersFile = new File("./config/blacklisted_servers.txt");

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

    @SubscribeEvent
    public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            LOGGER.info("User is in singleplayer, setting string length to 256.");
            GuiChatTransformer.maxChatLength = 256;
            return;
        }

        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (blacklistedServers.contains(serverIP)) {
            LOGGER.info("Current server supports 1.11+, but doesn't allow for 1.8.9 to use a high chat length, setting to 100.");
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
                    LOGGER.info("Server supports 1.11+, setting string length to 256.");
                    GuiChatTransformer.maxChatLength = 256;
                } else {
                    LOGGER.info("Server doesn't support 1.11+, setting string length to 100.");
                    GuiChatTransformer.maxChatLength = 100;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PatcherConfig getPatcherConfig() {
        return patcherConfig;
    }

    public PatcherSoundConfig getPatcherSoundConfig() {
        return patcherSoundConfig;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    @SuppressWarnings("unused")
    public CloudHandler getCloudHandler() {
        return cloudHandler;
    }

    public KeyBinding getNameHistory() {
        return nameHistory;
    }

    public KeyBinding getDropKeybind() {
        return dropKeybind;
    }
}
