package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

@SuppressWarnings("unused")
public class ForgeHooksClientHook {

    private static final FloatBuffer projectionMatrixOld = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer modelViewMatrixOld = BufferUtils.createFloatBuffer(16);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawScreenHead(GuiScreen screen) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixOld);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixOld);
            PatcherConfig.scaleOverride = PatcherConfig.currentScaleOverride;
            final ScaledResolution resolution = new ScaledResolution(mc);
            GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        }
    }

    public static void drawScreenReturn(GuiScreen screen) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            PatcherConfig.scaleOverride = -1;
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GL11.glLoadMatrix(projectionMatrixOld);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadMatrix(modelViewMatrixOld);
        }
    }

    public static int drawScreenMouseX(int mouseX) {
        if (mc.thePlayer != null && mc.currentScreen instanceof GuiContainer) {
            PatcherConfig.scaleOverride = PatcherConfig.currentScaleOverride;
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int width = resolution.getScaledWidth();
            mouseX = Mouse.getX() * width / mc.displayWidth;
        }

        return mouseX;
    }

    public static int drawScreenMouseY(int mouseY) {
        if (mc.thePlayer != null && mc.currentScreen instanceof GuiContainer) {
            PatcherConfig.scaleOverride = PatcherConfig.currentScaleOverride;
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int height = resolution.getScaledHeight();
            mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
        }

        return mouseY;
    }
}
