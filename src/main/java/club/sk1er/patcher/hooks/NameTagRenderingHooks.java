package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.FontRenderer;

public class NameTagRenderingHooks {
    public static int drawNametagText(FontRenderer fontRenderer, String text, int x, int y, int color) {
        if (PatcherConfig.shadowedNametagText) {
            return fontRenderer.drawStringWithShadow(text, x, y, color);
        } else {
            return fontRenderer.drawString(text, x, y, color);
        }
    }
}
