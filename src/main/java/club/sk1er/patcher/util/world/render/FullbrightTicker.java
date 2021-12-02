package club.sk1er.patcher.util.world.render;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.server.MinecraftServer;

//#if MC==11202
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.world.World;
//#endif

@SuppressWarnings("unused")
public class FullbrightTicker {

    public static boolean isFullbright() {
        //#if MC==10809
        MinecraftServer server = MinecraftServer.getServer();
        //#else
        //$$ World world = Minecraft.getMinecraft().world;
        //$$ if (world == null) return false;
        //$$ MinecraftServer server = world.getMinecraftServer();
        //#endif
        if (server != null && server.isCallingFromMinecraftThread()) {
            return false;
        }

        return PatcherConfig.fullbright;
    }
}
