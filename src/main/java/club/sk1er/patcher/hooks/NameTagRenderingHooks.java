package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class NameTagRenderingHooks {
    public static int drawNametagText(FontRenderer fontRenderer, String text, int x, int y, int color) {
        int i;
        if (PatcherConfig.shadowedNametagText) {
            GL11.glDepthMask(false);
            i = fontRenderer.renderString(text, x + 1.0F, y + 1.0F, color, true);
            GL11.glDepthMask(true);

            i = Math.max(i, fontRenderer.renderString(text, x, y, color, false));

            GL11.glColorMask(false, false, false, false);
            i = Math.max(i, fontRenderer.drawString(text, x, y, color, true));
            GL11.glColorMask(true, true, true, true);
        } else {
            i = fontRenderer.renderString(text, x, y, color, false);
        }

        return i;
    }
    
}
