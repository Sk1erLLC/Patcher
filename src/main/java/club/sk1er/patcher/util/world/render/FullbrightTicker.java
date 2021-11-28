package club.sk1er.patcher.util.world.render;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

//#if MC==11202
//$$ import net.minecraft.client.Minecraft;
//#endif

@SuppressWarnings("unused")
public class FullbrightTicker {

    public static boolean isFullbright() {
        //#if MC==10809
        MinecraftServer server = MinecraftServer.getServer();
        //#else
        //$$ MinecraftServer server = Minecraft.getMinecraft().world.getMinecraftServer();
        //#endif
        if (server != null && server.isCallingFromMinecraftThread()) {
            return false;
        }

        return PatcherConfig.fullbright;
    }
}
