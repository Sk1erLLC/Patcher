package club.sk1er.patcher.screen.render.caching;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.accessors.IGuiIngameForge;
import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class HUDCaching {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static Framebuffer framebuffer;
    private static boolean dirty = true;
    public static boolean renderingCacheOverride;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && PatcherConfig.hudCaching) {
            if (!OpenGlHelper.isFramebufferEnabled() && mc.thePlayer != null) {
                String statement = (!ClassTransformer.optifineVersion.equals("NONE") ?
                    "\n&cTry to disable OptiFine's Fast Render option." : "") + "\n&7Are Framebuffers supported?: &e&l" + OpenGlHelper.framebufferSupported;
                ChatUtilities.sendMessage("&cFramebuffers appear to be disabled, automatically disabling HUDCaching." + statement);
                PatcherConfig.hudCaching = false;
            } else {
                dirty = true;
            }
        }
    }

    @SuppressWarnings("unused")
    public static void renderCachedHud(EntityRenderer renderer, GuiIngame ingame, float partialTicks) {
        if (!OpenGlHelper.isFramebufferEnabled() || !PatcherConfig.hudCaching) {
            ingame.renderGameOverlay(partialTicks);
        } else {
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int width = resolution.getScaledWidth();
            final int height = resolution.getScaledHeight();
            renderer.setupOverlayRendering();
            GlStateManager.enableBlend();
            if (framebuffer != null) {
                if (ingame instanceof GuiIngameForge) {
                    ((IGuiIngameForge) ingame).renderCrosshairs(width, height);
                } else if (ingame.showCrosshair() && GuiIngameForge.renderCrosshairs) {
                    mc.getTextureManager().bindTexture(Gui.icons);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
                    GlStateManager.enableAlpha();
                    drawTexturedModalRect((width >> 1) - 7, (height >> 1) - 7);
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                }

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1, 1, 1, 1);
                framebuffer.bindFramebufferTexture();
                drawTexturedRect((float) resolution.getScaledWidth_double(), (float) resolution.getScaledHeight_double());
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            }

            if (framebuffer == null || dirty) {
                dirty = false;
                (framebuffer = checkFramebufferSizes(framebuffer, mc.displayWidth, mc.displayHeight)).framebufferClear();
                framebuffer.bindFramebuffer(false);
                GlStateManager.disableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                renderingCacheOverride = true;
                ingame.renderGameOverlay(partialTicks);
                renderingCacheOverride = false;
                mc.getFramebuffer().bindFramebuffer(false);
                GlStateManager.enableBlend();
            }
        }
    }

    private static Framebuffer checkFramebufferSizes(Framebuffer framebuffer, int width, int height) {
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer == null) {
                framebuffer = new Framebuffer(width, height, true);
                framebuffer.framebufferColor[0] = 0.0f;
                framebuffer.framebufferColor[1] = 0.0f;
                framebuffer.framebufferColor[2] = 0.0f;
            } else {
                framebuffer.createBindFramebuffer(width, height);
            }

            framebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        }

        return framebuffer;
    }

    private static void drawTexturedRect(float width, float height) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0, height, 0.0).tex(0, 0).endVertex();
        worldrenderer.pos(width, height, 0.0).tex(1, 0).endVertex();
        worldrenderer.pos(width, 0, 0.0).tex(1, 1).endVertex();
        worldrenderer.pos(0, 0, 0.0).tex(0, 1).endVertex();
        tessellator.draw();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    private static void drawTexturedModalRect(int x, int y) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + 16, 100.0).tex(0.0f, 0.0625f).endVertex();
        worldrenderer.pos(x + 16, y + 16, 100.0).tex(0.0625f, 0.0625f).endVertex();
        worldrenderer.pos(x + 16, y, 100.0).tex(0.0625f, 0.0f).endVertex();
        worldrenderer.pos(x, y, 100.0).tex(0.0f, 0.0f).endVertex();
        tessellator.draw();
    }
}
