package club.sk1er.patcher.hooks;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.MathHelper;

public class LayerCapeHook {

    public static void rotate(RenderPlayer playerRenderer, AbstractClientPlayer entitylivingbaseIn, float height, float swing, float swingSides) {
        float v = (float) ((swingSides / 2) / Math.sqrt(2 + (Math.pow((swingSides - 10) / 60, 2))));

        float angle1 = MathHelper.clamp_float((swing / ((float) Math.sqrt(5 + (Math.pow(swing / 150, 2))))) + height, entitylivingbaseIn.isSneaking() ? 32.0F + height : 5.0F, 130.0F);
        float angle2 = MathHelper.clamp_float(v, -50.0F, 65.0F);
        float angle3 = MathHelper.clamp_float(-v, -50.0F, 65.0F);

        GlStateManager.rotate(angle1, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angle2, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(angle3, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        playerRenderer.getMainModel().renderCape(0.0625F);
        GlStateManager.popMatrix();
    }
}
