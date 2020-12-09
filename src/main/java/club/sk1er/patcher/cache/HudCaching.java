package club.sk1er.patcher.cache;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.accessors.IGuiIngameForge;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

public class HudCaching {
    /*private static final Minecraft mc = Minecraft.getMinecraft();
    private static Framebuffer framebuffer = null;

    private static int tickCounter = 0;
    private static int rendersThisTick = 0;
    public static boolean renderingCacheOverride = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        tickCounter--;
        rendersThisTick = 0;
    }

    public static void setupGlState() {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
    }

    public static void renderCachedHud(EntityRenderer entityRenderer, GuiIngame ingame, float partialTicks) {
        if (!OpenGlHelper.isFramebufferEnabled() || !PatcherConfig.hudCaching) {
            ingame.renderGameOverlay(partialTicks);
        } else {
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int width = resolution.getScaledWidth();
            final int height = resolution.getScaledHeight();
            final double widthD = resolution.getScaledWidth_double();
            final double heightD = resolution.getScaledHeight_double();

            entityRenderer.setupOverlayRendering();
            GlStateManager.enableBlend();

            ingame.renderVignette(mc.thePlayer.getBrightness(partialTicks), resolution);

            final int cacheTimer = PatcherConfig.hudCacheTimer;
            if (framebuffer == null || (cacheTimer >= 1000 && tickCounter <= 0) ||
                (cacheTimer < 1000 && partialTicks * 1000 > rendersThisTick * cacheTimer)) {
                tickCounter = cacheTimer / 1000;
                rendersThisTick++;

                framebuffer = checkFramebufferSizes(framebuffer, mc.displayWidth, mc.displayHeight);

                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(false);

                GlStateManager.disableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableLighting();
                GlStateManager.disableFog();

                renderingCacheOverride = true;
                ingame.renderGameOverlay(partialTicks);
                renderingCacheOverride = false;

                mc.getFramebuffer().bindFramebuffer(false);
                GlStateManager.enableBlend();
            }

            if (ingame instanceof GuiIngameForge) {
                ((IGuiIngameForge) ingame).renderCrosshairs(width, height);
            } else if (ingame.showCrosshair()) {
                mc.getTextureManager().bindTexture(Gui.icons);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
                GlStateManager.enableAlpha();
                drawTexturedModalRect(width / 2 - 7, height / 2 - 7);
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            }

            if (ingame instanceof GuiIngameForge && PatcherConfig.hudCachingCompatibilityMode) {
                final GuiIngameForge forge = (GuiIngameForge) ingame;

                if (!pre(forge, ALL)) {
                    if (GuiIngameForge.renderHelmet && !pre(forge, HELMET)) post(forge, HELMET);
                    if (GuiIngameForge.renderPortal && !pre(forge, PORTAL)) post(forge, PORTAL);
                    if (GuiIngameForge.renderHotbar && !pre(forge, HOTBAR)) post(forge, HOTBAR);
                    if (GuiIngameForge.renderCrosshairs && !pre(forge, CROSSHAIRS)) post(forge, CROSSHAIRS);
                    if (GuiIngameForge.renderBossHealth && !pre(forge, BOSSHEALTH)) post(forge, BOSSHEALTH);

                    if (mc.playerController.shouldDrawHUD() && mc.getRenderViewEntity() instanceof EntityPlayer) {
                        if (GuiIngameForge.renderHealth && !pre(forge, HEALTH)) post(forge, HEALTH);
                        if (GuiIngameForge.renderArmor && !pre(forge, ARMOR)) post(forge, ARMOR);
                        if (GuiIngameForge.renderFood && !pre(forge, FOOD)) post(forge, FOOD);
                        if (GuiIngameForge.renderHealthMount && !pre(forge, HEALTHMOUNT)) post(forge, HEALTHMOUNT);
                        if (GuiIngameForge.renderAir && !pre(forge, AIR)) post(forge, AIR);
                    }

                    if (GuiIngameForge.renderJumpBar && !pre(forge, JUMPBAR)) post(forge, JUMPBAR);
                    if (GuiIngameForge.renderExperiance && !pre(forge, EXPERIENCE)) post(forge, EXPERIENCE);
                    if (!pre(forge, DEBUG)) post(forge, DEBUG);
                    if (!pre(forge, TEXT)) post(forge, TEXT);
                    if (!pre(forge, CHAT)) post(forge, CHAT);
                    if (!pre(forge, PLAYER_LIST)) post(forge, PLAYER_LIST);

                    post(forge, ALL);
                }
            }

            framebuffer.bindFramebufferTexture();
            drawTexturedRect(0, 0, (float) widthD, (float) heightD, 0, 1, 1, 0, GL11.GL_NEAREST);
        }
    }

    private static void drawTexturedModalRect(int x, int y) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + 16, 100).tex(0.0f, 0.0625f).endVertex();
        worldrenderer.pos(x + 16, y + 16, 100).tex(0.0625f, 0.0625f).endVertex();
        worldrenderer.pos(x + 16, y, 100).tex(0.0625f, 0.0f).endVertex();
        worldrenderer.pos(x, y, 100).tex(0.0f, 0.0f).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
            .pos(x, y + height, 0.0D)
            .tex(uMin, vMax).endVertex();
        worldrenderer
            .pos(x + width, y + height, 0.0D)
            .tex(uMax, vMax).endVertex();
        worldrenderer
            .pos(x + width, y, 0.0D)
            .tex(uMax, vMin).endVertex();
        worldrenderer
            .pos(x, y, 0.0D)
            .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GlStateManager.disableBlend();
    }

    private static Framebuffer checkFramebufferSizes(Framebuffer framebuffer, int width, int height) {
        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer == null) {
                framebuffer = new Framebuffer(width, height, true);
            } else {
                framebuffer.createBindFramebuffer(width, height);
            }

            framebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        }

        return framebuffer;
    }

    private static boolean pre(GuiIngameForge forge, RenderGameOverlayEvent.ElementType type) {
        setupGlState();
        return ((IGuiIngameForge) forge).pre(type);
    }

    private static void post(GuiIngameForge forge, RenderGameOverlayEvent.ElementType type) {
        ((IGuiIngameForge) forge).post(type);
    }*/
}
