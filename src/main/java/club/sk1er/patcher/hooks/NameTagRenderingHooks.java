package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.FontRendererAccessor;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class NameTagRenderingHooks {
    public static int drawNametagText(FontRenderer fr, String text, int x, int y, int color) {
        FontRendererAccessor fontRenderer = (FontRendererAccessor) fr;
        int render;
        if (PatcherConfig.shadowedNametagText) {
            GL11.glDepthMask(false);
            render = fontRenderer.callRenderString(text, PatcherConfig.alternateTextShadow ? x : x + 1.0F, y + 1.0F, color, true);
            GL11.glDepthMask(true);

            render = Math.max(render, fontRenderer.callRenderString(text, x, y, color, false));

            GL11.glColorMask(false, false, false, false);
            render = Math.max(render, fr.drawString(text, x, y, color, true));
            GL11.glColorMask(true, true, true, true);
        } else {
            render = fontRenderer.callRenderString(text, x, y, color, false);
        }

        return render;
    }
    
}
