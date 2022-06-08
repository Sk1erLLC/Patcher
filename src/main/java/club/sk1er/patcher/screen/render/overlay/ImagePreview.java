package club.sk1er.patcher.screen.render.overlay;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Sets;
import gg.essential.api.EssentialAPI;
import gg.essential.api.utils.Multithreading;
import gg.essential.api.utils.TrustedHostsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class ImagePreview {

    private final Minecraft mc = Minecraft.getMinecraft();
    private BufferedImage image;
    private String loaded;
    private int tex = -1;
    private int width = 100;
    private int height = 100;

    private final Set<String> trustedHosts = Sets.newHashSet(
        "cdn.discordapp.com", "media.discordapp.net",
        "i.badlion.net",
        "i.imgur.com", "imgur.com",
        "sk1er.exposed", "inv.wtf", "i.inv.wtf",
        "i.redd.it",
        "pbs.twimg.com"
    );

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PatcherConfig.imagePreview) return;

        IChatComponent chatComponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (chatComponent == null) return;

        ChatStyle chatStyle = chatComponent.getChatStyle();
        ClickEvent chatClickEvent = chatStyle.getChatClickEvent();
        if (chatStyle.getChatClickEvent() != null && chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            handle(chatClickEvent.getValue());
        }
    }

    private void handle(String value) {
        try {
            URL url = new URL(value);
            String host = url.getHost();
            if (!this.isHostTrusted(host)) return;
        } catch (MalformedURLException e) {
            return;
        }

        if (!value.startsWith("http")) {
            if (tex != -1) GlStateManager.deleteTexture(tex);
            tex = -1;
            return;
        }

        if (value.contains("imgur.com/")) {
            String[] split = value.split("/");
            value = String.format("https://i.imgur.com/%s.png", split[split.length - 1]);
        }

        if (!value.equals(loaded)) {
            loaded = value;

            if (tex != -1) GlStateManager.deleteTexture(tex);

            tex = -1;
            String finalValue = value;
            Multithreading.runAsync(() -> loadUrl(finalValue));
        }

        if (this.image != null) {
            DynamicTexture dynamicTexture = new DynamicTexture(image);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            dynamicTexture.updateDynamicTexture();
            this.tex = dynamicTexture.getGlTextureId();
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.image = null;
        }

        if (tex != -1) {
            GlStateManager.pushMatrix();
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int scaleFactor = scaledResolution.getScaleFactor();
            float inverseScale = 1 / (float) scaleFactor;
            GlStateManager.scale(inverseScale, inverseScale, inverseScale);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(tex);
            GlStateManager.color(1, 1, 1, 1);
            float scaleWidth = scaledResolution.getScaledWidth() * scaleFactor;

            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) scaleWidth *= PatcherConfig.imagePreviewWidth;
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) scaleWidth = this.width;

            float aspectRatio = width / (float) height;
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
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x, y + height, 0).tex(0, 1).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(x + width, y + height, 0).tex(1, 1).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(x + width, y, 0).tex(1, 0).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(x, y, 0).tex(0, 0).color(255, 255, 255, 255).endVertex();
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

            try (InputStream stream = connection.getInputStream()) {
                image = TextureUtil.readBufferedImage(stream);
            }
        } catch (Exception e) {
            Patcher.instance.getLogger().error("Failed to load an image preview from {}", url, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private boolean isHostTrusted(String host) {
        for (String trustedHost : this.trustedHosts) {
            if (host.equalsIgnoreCase(trustedHost)) {
                return true;
            }
        }

        // Essential's Trusted Host can be added to by people through Friend Messages, which can include some
        // domains that we may not know or have in our trustedHosts set, so if ours fails to catch the domain
        // then it should attempt to go through Essential as well.
        for (TrustedHostsUtil.TrustedHost trustedHost : EssentialAPI.getTrustedHostsUtil().getTrustedHosts()) {
            for (String domain : trustedHost.getDomains()) {
                if (host.equalsIgnoreCase(domain)) {
                    return true;
                }
            }
        }

        return false;
    }
}
