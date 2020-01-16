package club.sk1er.patcher;

import club.sk1er.patcher.command.PatcherCommand;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.scheduler.ScreenHandler;
import club.sk1er.patcher.sound.SoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "patcher", name = "Patcher", version = "1.0")
public class Patcher {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PatcherConfig.instance.preload();
        MinecraftForge.EVENT_BUS.register(new ScreenHandler());
        SoundHandler target = new SoundHandler();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(target);
        MinecraftForge.EVENT_BUS.register(target);
        ClientCommandHandler.instance.registerCommand(new PatcherCommand());
    }
}
