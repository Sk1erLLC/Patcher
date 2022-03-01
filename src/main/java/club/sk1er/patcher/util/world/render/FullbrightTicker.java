package club.sk1er.patcher.util.world.render;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

//#if MC==11202
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.server.integrated.IntegratedServer;
//#endif

public class FullbrightTicker {

    public static boolean isFullbright() {
        //#if MC==10809
        MinecraftServer server = MinecraftServer.getServer();
        //#else
        //$$ IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        //#endif
        if (server != null && server.isCallingFromMinecraftThread()) {
            return false;
        }

        return PatcherConfig.fullbright;
    }
}
