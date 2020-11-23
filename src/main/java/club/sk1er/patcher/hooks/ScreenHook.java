package club.sk1er.patcher.hooks;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ScreenHook {

    private static int glList;

    public static void start() {

        if (glList == 0)
            glList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glList, GL11.GL_COMPILE_AND_EXECUTE);

//        GlStateManager.pushMatrix();
//        GlStateManager.translate(-250, 0, -750);
//        GlStateManager.enableDepth();
//        GlStateManager.depthMask(true);
//        GlStateManager.depthFunc(GL11.GL_GEQUAL);
    }

    public static void end() {
//        GlStateManager.popMatrix();
//        GlStateManager.disableDepth();
//        GlStateManager.depthFunc(515);
        GL11.glEndList();
    }

    public static void startR() {


//        GlStateManager.pushMatrix();
//        GlStateManager.enableDepth();
//        GlStateManager.depthMask(true);
//        GlStateManager.translate(0, 0, -50);
//        GlStateManager.depthFunc(GL11.GL_GREATER);
    }

    public static void endR() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-250,0,0);
        GlStateManager.callList(glList);
        GlStateManager.popMatrix();
//        GlStateManager.popMatrix();
//        GlStateManager.disableDepth();
//        GlStateManager.depthFunc(515);
    }
}
