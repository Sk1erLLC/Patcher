package club.sk1er.patcher;

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.command.FovChangerCommand;
import club.sk1er.patcher.command.NameHistoryCommand;
import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.command.PatcherSoundsCommand;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.config.PatcherSoundConfig;
import club.sk1er.patcher.keybind.KeybindDropStack;
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
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "patcher", name = "Patcher", version = "1.1")
public class Patcher {

    private final Logger LOGGER = LogManager.getLogger("Patcher");
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
        ClientCommandHandler.instance.registerCommand(new PatcherCommand());
        ClientCommandHandler.instance.registerCommand(new PatcherSoundsCommand());
        ClientCommandHandler.instance.registerCommand(new FovChangerCommand()); // ve replacement
        ClientCommandHandler.instance.registerCommand(new NameHistoryCommand());

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(target);
        MinecraftForge.EVENT_BUS.register(new TabToggleHandler());
        MinecraftForge.EVENT_BUS.register(new EntityRendering());
        MinecraftForge.EVENT_BUS.register(new FovHandler());
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
        MinecraftForge.EVENT_BUS.register(new HotbarItemsHandler());
        MinecraftForge.EVENT_BUS.register(new EntityCulling());
        MinecraftForge.EVENT_BUS.register(new ArmorStatusRenderer());
        MinecraftForge.EVENT_BUS.register(new KeybindDropStack());
        MinecraftForge.EVENT_BUS.register(new EntityTrace());
        MinecraftForge.EVENT_BUS.register(cloudHandler = new CloudHandler());
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
        if (serverIP.contains("mineplex")) {
            LOGGER.info("Mineplex doesn't allow for 1.8.9 to use a high chat length, setting to 100.");
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

    public CloudHandler getCloudHandler() {
        return cloudHandler;
    }
}
