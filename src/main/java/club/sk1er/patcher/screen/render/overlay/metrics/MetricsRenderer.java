package club.sk1er.patcher.screen.render.overlay.metrics;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.MinecraftHook;
import club.sk1er.patcher.hooks.MinecraftServerHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class MetricsRenderer extends Gui {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void draw(RenderGameOverlayEvent.Post event) {
        if (PatcherConfig.useVanillaMetricsRenderer && event.type == RenderGameOverlayEvent.ElementType.DEBUG && mc.gameSettings.showLagometer && mc.gameSettings.showDebugInfo) {
            final ScaledResolution resolution = new ScaledResolution(mc);
            final int width = resolution.getScaledWidth();
            this.drawMetricsData(resolution, mc.fontRendererObj, MinecraftHook.metricsData, 0, width >> 1, true);
            if (mc.getIntegratedServer() != null) {
                this.drawMetricsData(resolution, mc.fontRendererObj, MinecraftServerHook.metricsData, width - Math.min(width >> 1, 240), width >> 1, false);
            }
        }
    }

    public void drawMetricsData(ScaledResolution resolution, FontRenderer fontRenderer, MetricsData data, int x, int width, boolean showFps) {
        final int startIndex = data.getStartIndex();
        final int writeIndex = data.getWriteIndex();
        final long[] samples = data.getSamples();
        final int maxSamples = Math.max(0, samples.length - width);
        final int sampleAverage = samples.length - maxSamples;
        int modifiedX = x;
        int startWrap = data.wrapIndex(startIndex + maxSamples);
        long o = 0L; // todo what is this
        int maxValue = Integer.MAX_VALUE;
        int minValue = Integer.MIN_VALUE;

        for (int sampleLength = 0; sampleLength < sampleAverage; ++sampleLength) {
            final int wrappedIndex = (int) (samples[data.wrapIndex(startWrap + sampleLength)] / 1000000L);
            maxValue = Math.min(maxValue, wrappedIndex);
            minValue = Math.max(minValue, wrappedIndex);
            o += wrappedIndex;
        }

        final int scaledHeight = resolution.getScaledHeight();
        drawRect(x, scaledHeight - 60, x + sampleAverage, scaledHeight, -1873784752);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer renderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO);
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (; startWrap != writeIndex; startWrap = data.wrapIndex(startWrap + 1)) {
            final int valuePosition = data.scaleSampleTo(samples[startWrap], showFps ? 30 : 60, showFps ? 60 : 20);
            final int colorValue = showFps ? 100 : 60;
            final int lineColor = this.getMetricsLineColor(MathHelper.clamp_int(valuePosition, 0, colorValue), colorValue / 2, colorValue);
            final int alpha = lineColor >> 24 & 255;
            final int red = lineColor >> 16 & 255;
            final int green = lineColor >> 8 & 255;
            final int blue = lineColor & 255;
            renderer.pos(modifiedX + 1, scaledHeight, 0).color(red, green, blue, alpha).endVertex();
            renderer.pos(modifiedX + 1, scaledHeight - valuePosition + 1, 0).color(red, green, blue, alpha).endVertex();
            renderer.pos(modifiedX, scaledHeight - valuePosition + 1, 0).color(red, green, blue, alpha).endVertex();
            renderer.pos(modifiedX, scaledHeight, 0).color(red, green, blue, alpha).endVertex();
            ++modifiedX;
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        if (showFps) {
            drawRect(x + 1, scaledHeight - 30 + 1, x + 14, scaledHeight - 30 + 10, -1873784752);
            fontRenderer.drawString("60 FPS", x + 2, scaledHeight - 30 + 2, 14737632);
            drawHorizontalLine(x, x + sampleAverage - 1, scaledHeight - 30, -1);
            drawRect(x + 1, scaledHeight - 60 + 1, x + 14, scaledHeight - 60 + 10, -1873784752);
            fontRenderer.drawString("30 FPS", x + 2, scaledHeight - 60 + 2, 14737632);
        } else {
            drawRect(x + 1, scaledHeight - 60 + 1, x + 14, scaledHeight - 60 + 10, -1873784752);
            fontRenderer.drawString("20 TPS", x + 2, scaledHeight - 60 + 2, 14737632);
        }

        drawHorizontalLine(x, x + sampleAverage - 1, scaledHeight - 60, -1);
        drawHorizontalLine(x, x + sampleAverage - 1, scaledHeight - 1, -1);
        drawVerticalLine(x, scaledHeight - 60, scaledHeight, -1);
        drawVerticalLine(x + sampleAverage - 1, scaledHeight - 60, scaledHeight, -1);
        if (showFps && mc.gameSettings.limitFramerate > 0 && mc.gameSettings.limitFramerate <= 250) {
            drawHorizontalLine(x, x + sampleAverage - 1, scaledHeight - 1 - (1800 / mc.gameSettings.limitFramerate), -16711681);
        }

        final String minMs = maxValue + " ms min";
        final String msAvg = o / sampleAverage + " ms avg";
        final String msMax = minValue + " ms max";
        final int textHeight = (scaledHeight - 60) - 9;
        fontRenderer.drawStringWithShadow(minMs, x + 2, textHeight, 14737632);
        fontRenderer.drawStringWithShadow(msAvg, x + (sampleAverage >> 1) - (fontRenderer.getStringWidth(msAvg) >> 1), textHeight, 14737632);
        fontRenderer.drawStringWithShadow(msMax, x + sampleAverage - fontRenderer.getStringWidth(msMax), textHeight, 14737632);
        GlStateManager.enableDepth();
    }

    private int getMetricsLineColor(int value, int yellowValue, int redValue) {
        return value < yellowValue
            ? this.interpolateColor(-16711936, -256, (float) value / (float) yellowValue)
            : this.interpolateColor(-256, -65536, (float) (value - yellowValue) / (float) (redValue - yellowValue));
    }

    private int interpolateColor(int minColor, int maxColor, float delta) {
        final int minAlpha = minColor >> 24 & 255;
        final int minRed = minColor >> 16 & 255;
        final int minGreen = minColor >> 8 & 255;
        final int minBlue = minColor & 255;
        final int maxAlpha = maxColor >> 24 & 255;
        final int maxRed = maxColor >> 16 & 255;
        final int maxGreen = maxColor >> 8 & 255;
        final int maxBlue = maxColor & 255;
        final int clampAlpha = MathHelper.clamp_int((int) lerp(delta, minAlpha, maxAlpha), 0, 255);
        final int clampRed = MathHelper.clamp_int((int) lerp(delta, minRed, maxRed), 0, 255);
        final int clampGreen = MathHelper.clamp_int((int) lerp(delta, minGreen, maxGreen), 0, 255);
        final int clampBlue = MathHelper.clamp_int((int) lerp(delta, minBlue, maxBlue), 0, 255);
        return clampAlpha << 24 | clampRed << 16 | clampGreen << 8 | clampBlue;
    }

    private float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }
}
