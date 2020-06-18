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

package club.sk1er.patcher.util.chat;

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImagePreview {

    private final String[] ALLOWED_HOSTS = {"sk1er.exposed", "imgur.com", "i.imgur.com", "i.badlion.net"};
    private final DecimalFormat format = new DecimalFormat("#.00");
    private final List<Long> frames = new ArrayList<>();
    private final String[] renderStrings = new String[5];
    private String loaded;
    private int tex = -1;
    private int width = 100;
    private int height = 100;
    private BufferedImage image;
    private boolean frameRender = false;
    private long updated = 0;
    private String mode = "???";

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (frameRender) {
            frames.add(System.currentTimeMillis());
            frames.removeIf(aLong -> System.currentTimeMillis() - aLong > TimeUnit.MINUTES.toMillis(1));
            if (System.currentTimeMillis() - updated > TimeUnit.SECONDS.toMillis(1)) {
                updated = System.currentTimeMillis();
                renderStrings[0] = "Mode: " + mode;
                int[] intervals = new int[]{1, 10, 30, 60};
                int e = 0;
                for (int interval : intervals) {
                    int amt = 0;
                    for (Long frame : frames) {
                        if (System.currentTimeMillis() - frame < TimeUnit.SECONDS.toMillis(interval)) amt++;
                    }
                    renderStrings[++e] = "Avg on " + interval + "s: " + format.format(amt / ((float) interval));
                }
            }
            final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            final FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
            int y = 40;
            for (String render : renderStrings) {
                font.drawString(render, scaledResolution.getScaledWidth() - 5 - font.getStringWidth(render), y, Color.RED.getRGB(), true);
                y += 10;
            }
        }
        if (!PatcherConfig.imagePreview) return;
        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        final IChatComponent chatComponent = chat.getChatComponent(Mouse.getX(), Mouse.getY());
        if (chatComponent != null) {
            final ChatStyle chatStyle = chatComponent.getChatStyle();
            final ClickEvent chatClickEvent = chatStyle.getChatClickEvent();
            if (chatStyle.getChatClickEvent() != null && chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                handle(chatClickEvent.getValue());
            }
        }
    }

    private void handle(String value) {

        try {
            final URL url = new URL(value);
            final String host = url.getHost();
            boolean found = false;
            for (String allowed_host : ALLOWED_HOSTS) {
                if (host.equalsIgnoreCase(allowed_host)) {
                    found = true;
                    break;
                }
            }
            if (!found) return;
        } catch (MalformedURLException e) {
            return;
        }
        if (!value.startsWith("http")) {
            if (tex != -1)
                GlStateManager.deleteTexture(tex);
            tex = -1;
            return;
        }
        if (value.contains("imgur.com/")) {
            final String[] split = value.split("/");
            value = String.format("https://i.imgur.com/%s.png", split[split.length - 1]);
        }
        if (!value.equals(loaded)) {
            loaded = value;
            if (tex != -1)
                GlStateManager.deleteTexture(tex);
            tex = -1;
            String finalValue = value;
            Multithreading.runAsync(() -> loadUrl(finalValue));
        }
        if (this.image != null) {
            final DynamicTexture dynamicTexture = new DynamicTexture(image);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            dynamicTexture.updateDynamicTexture();
            this.tex = dynamicTexture.getGlTextureId();
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.image = null;

        }
        if (tex != -1) {
            GlStateManager.pushMatrix();
            final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            final int scaleFactor = scaledResolution.getScaleFactor();
            final float i = 1 / ((float) scaleFactor);
            GlStateManager.scale(i, i, i);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(tex);
            GlStateManager.color(1, 1, 1, 1);
            float aspectRatio = width / (float) height;
            float scaleWidth = scaledResolution.getScaledWidth() * scaleFactor;
            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                scaleWidth *= PatcherConfig.imagePreviewWidth / 100D;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                scaleWidth = this.width;
            }
            float maxWidth = scaleWidth;
            float height = maxWidth / aspectRatio;
            if (height > scaledResolution.getScaledHeight() * scaleFactor) {
                height = scaledResolution.getScaledHeight() * scaleFactor;
                maxWidth = height * aspectRatio;
            }
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            drawTexturedModalRect(0, 0, (int) maxWidth, (int) height);
            GlStateManager.popMatrix();
        }
    }

    public void drawTexturedModalRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0).tex(0, 1).endVertex();
        worldrenderer.pos((x + width), (y + height), 0).tex(1, 1).endVertex();
        worldrenderer.pos((x + width), (y), 0).tex(1, 0).endVertex();
        worldrenderer.pos((x), (y), 0).tex(0, 0).endVertex();
        tessellator.draw();
    }

    private void loadUrl(String url) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.addRequestProperty("User-Agent", "Patcher Image Previewer");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            image = TextureUtil.readBufferedImage(connection.getInputStream());
        } catch (IOException ignored) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public List<Long> getFrames() {
        return frames;
    }

    public void toggleFPS() {
        this.frameRender = !this.frameRender;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
