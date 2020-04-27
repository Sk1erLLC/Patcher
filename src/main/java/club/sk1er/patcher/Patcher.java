package club.sk1er.patcher;

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.command.FovChangerCommand;
import club.sk1er.patcher.command.NameHistoryCommand;
import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.command.PatcherSoundsCommand;
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
import club.sk1er.patcher.util.keybind.KeybindBuilder;
import club.sk1er.patcher.util.screen.MainMenuEditor;
import club.sk1er.patcher.util.screenshot.AsyncScreenshots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = "1.1")
public class Patcher {

    private final Logger LOGGER = LogManager.getLogger("Patcher");
    private final File logsDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "/" + File.separator + "logs" + File.separator);
    private PatcherConfig patcherConfig;
    private PatcherSoundConfig patcherSoundConfig;
    private CloudHandler cloudHandler;

    @Mod.Instance("patcher")
    public static Patcher instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        patcherConfig = new PatcherConfig();
        patcherConfig.preload();

        patcherSoundConfig = new PatcherSoundConfig();
        patcherSoundConfig.preload();

        SoundHandler target = new SoundHandler();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(target);

        KeybindBuilder.instance.registerPatcherKeybinds();

        ClientCommandHandler.instance.registerCommand(new PatcherCommand());
        ClientCommandHandler.instance.registerCommand(new PatcherSoundsCommand());
        ClientCommandHandler.instance.registerCommand(new FovChangerCommand()); // ve replacement
        ClientCommandHandler.instance.registerCommand(new NameHistoryCommand());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.FavoriteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.DeleteScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.UploadScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.CopyScreenshot());
        ClientCommandHandler.instance.registerCommand(new AsyncScreenshots.ScreenshotsFolder());

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
        registerClass(new MainMenuEditor());
        registerClass(cloudHandler = new CloudHandler());

        checkLogs();
    }

    private void checkLogs() {
        if (PatcherConfig.logOptimizer) {
            for (File file : Objects.requireNonNull(logsDirectory.listFiles())) {
                if (file.lastModified() <= (System.currentTimeMillis() - PatcherConfig.logOptimizerLength * 24 * 60 * 60 * 1000)) {
                    LOGGER.info("Deleted " + file.getName() + ", last modified was " + file.lastModified());
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

    @SubscribeEvent
    public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            LOGGER.info("User is in singleplayer, setting string length to 256.");
            GuiChatTransformer.maxChatLength = 256;
            return;
        }

        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (serverIP.contains("mineplex") || serverIP.contains("mccentral")) {
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

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
//        ScreenShotHelper.pixelValues = null; //Reset because this uses 14 mb of persistent ram after screenshot is taken
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

    private static boolean cacheDevelopment;

    public static boolean isDevelopment() {
        if (cacheDevelopment) {
            return true;
        } else {
            Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
            if (o == null) return false;
            return cacheDevelopment = (boolean) o;
        }
    }
}
