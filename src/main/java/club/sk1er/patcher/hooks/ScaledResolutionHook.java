package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class ScaledResolutionHook {
    public static int modifyGuiScale() {
        final int scale = PatcherConfig.scaleOverride;
        return scale >= 0 ? scale : Minecraft.getMinecraft().gameSettings.guiScale;
    }
}
