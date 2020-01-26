package club.sk1er.patcher;

import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.scheduler.ScreenHandler;
import club.sk1er.patcher.sound.SoundHandler;
import club.sk1er.patcher.status.ProtocolDetector;
import club.sk1er.patcher.util.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mod(modid = "patcher", name = "Patcher", version = "1.0")
public class Patcher {

    public static boolean allowsHigherChatLength;
    private final Logger LOGGER = LogManager.getLogger("Patcher");
    private PatcherConfig patcherConfig;

    @Mod.Instance("patcher")
    public static Patcher instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        patcherConfig = new PatcherConfig(new File(Minecraft.getMinecraft().mcDataDir, "patcher.toml"));
        patcherConfig.preload();

        SoundHandler target = new SoundHandler();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(target);
        ClientCommandHandler.instance.registerCommand(new PatcherCommand());

        MinecraftForge.EVENT_BUS.register(target);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ScreenHandler());
    }

    @SubscribeEvent
    public void connectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            LOGGER.info("User is in singleplayer.");
            allowsHigherChatLength = false;
            return;
        }

        CompletableFuture<Boolean> future = ProtocolDetector.instance.isCompatibleWithVersion(
                Minecraft.getMinecraft().getCurrentServerData().serverIP,
                315 // 1.11
        );

        Multithreading.runAsync(() -> {
            try {
                if (future.get()) {
                    LOGGER.info("Server supports 1.11+!");
                    allowsHigherChatLength = true;
                } else {
                    LOGGER.info("Server doesn't support 1.11+.");
                    allowsHigherChatLength = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PatcherConfig getPatcherConfig() {
        return patcherConfig;
    }
}
