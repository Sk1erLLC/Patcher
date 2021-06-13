package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

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

    public static int fixHealthMargin(int original) {
        if (mc.thePlayer.isPotionActive(Potion.poison)) original -= 36;
        else if (mc.thePlayer.isPotionActive(Potion.wither)) original -= 108;
        return original;
    }
}
