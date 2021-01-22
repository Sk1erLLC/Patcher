package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class GuiIngameForgeHook {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawActionbarText(String recordPlaying, int color) {
        mc.fontRendererObj.drawString(
            recordPlaying,
            -mc.fontRendererObj.getStringWidth(recordPlaying) >> 1, -4,
            color, PatcherConfig.shadowedActionbarText
        );
    }
}
