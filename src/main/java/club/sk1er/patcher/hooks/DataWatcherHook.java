package club.sk1er.patcher.hooks;

import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class DataWatcherHook {
    public static boolean checkWorldStatus() {
        return Minecraft.getMinecraft().theWorld == null || !Minecraft.getMinecraft().theWorld.isRemote;
    }
}
