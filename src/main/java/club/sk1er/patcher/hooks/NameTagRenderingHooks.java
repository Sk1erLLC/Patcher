package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class NameTagRenderingHooks {
    public static int drawNametagText(FontRenderer fontRenderer, String text, int x, int y, int color) {
        int render;
        if (PatcherConfig.shadowedNametagText) {
            GL11.glDepthMask(false);
            render = fontRenderer.renderString(text, PatcherConfig.alternateTextShadow ? x : x + 1.0F, y + 1.0F, color, true);
            GL11.glDepthMask(true);

            render = Math.max(render, fontRenderer.renderString(text, x, y, color, false));

            GL11.glColorMask(false, false, false, false);
            render = Math.max(render, fontRenderer.drawString(text, x, y, color, true));
            GL11.glColorMask(true, true, true, true);
        } else {
            render = fontRenderer.renderString(text, x, y, color, false);
        }

        return render;
    }
    
}
