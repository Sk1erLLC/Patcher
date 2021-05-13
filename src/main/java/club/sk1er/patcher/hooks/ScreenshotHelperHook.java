/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.hooks;

import net.modcore.api.utils.Multithreading;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.asm.resources.ScreenShotHelperTransformer;
import club.sk1er.patcher.util.screenshot.AsyncScreenshots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.nio.IntBuffer;

/**
 * Used in {@link ScreenShotHelperTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class ScreenshotHelperHook {
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer framebuffer) {
        File screenshotDirectory = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
        if (!screenshotDirectory.exists()) {
            screenshotDirectory.mkdir();
        }

        if (OpenGlHelper.isFramebufferEnabled()) {
            width = framebuffer.framebufferTextureWidth;
            height = framebuffer.framebufferTextureHeight;
        }

        int scale = width * height;

        if (pixelBuffer == null || pixelBuffer.capacity() < scale) {
            pixelBuffer = BufferUtils.createIntBuffer(scale);
            pixelValues = new int[scale];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(framebuffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        Multithreading.runAsync(new AsyncScreenshots(width, height, pixelValues, framebuffer, screenshotDirectory));

        if (Minecraft.getMinecraft().thePlayer != null && !PatcherConfig.screenshotNoFeedback) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(" "));
        }

        return new ChatComponentText(AsyncScreenshots.prefix + "Capturing screenshot.");
    }
}
