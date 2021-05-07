package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;

@SuppressWarnings("unused")
public class GuiIngameHook {
    public static void colorVignette() {
        if (PatcherConfig.replaceNausea && Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.confusion)) {
            GlStateManager.color(1f, 0, 1f, 1f);
        }
    }
}
