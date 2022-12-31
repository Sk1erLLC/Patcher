package club.sk1er.patcher.screen.render.overlay;

import cc.polyfrost.oneconfig.utils.Multithreading;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import gg.essential.api.EssentialAPI;
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
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagePreview {
    private final Pattern OGP_IMAGE_REGEX = Pattern.compile("<meta property=\"(?:og:image|twitter:image)\" content=\"(?<url>.+?)\".*?/?>");
    private final Pattern IMG_TAG_REGEX = Pattern.compile("<img.*?src=\"(?<url>.+?)\".*?>");

    private final Minecraft mc = Minecraft.getMinecraft();
    private BufferedImage image;
    private String loaded;

    private int tex = -1;
    private int width = 100;
    private int height = 100;

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PatcherConfig.imagePreview) return;
        final IChatComponent chatComponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (chatComponent != null) {
            final ChatStyle chatStyle = chatComponent.getChatStyle();
            final ClickEvent chatClickEvent = chatStyle.getChatClickEvent();
            if (chatStyle.getChatClickEvent() != null && chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                handle(chatClickEvent.getValue());
            }
        }
    }

    private void handle(String value) {
        if (Patcher.instance.isEssential()) {
            try {
                final URL url = new URL(value);
                final String host = url.getHost();
                boolean found = false;

                for (TrustedHostsUtil.TrustedHost trustedHost : EssentialAPI.getTrustedHostsUtil().getTrustedHosts()) {
                    for (String domain : trustedHost.getDomains()) {
                        if (host.equalsIgnoreCase(domain)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) return;
            } catch (MalformedURLException e) {
                return;
            }
        }

        if (!value.startsWith("http")) {
            if (tex != -1) GlStateManager.deleteTexture(tex);
            tex = -1;
            return;
        }

        if (value.contains("imgur.com/")) {
            final String[] split = value.split("/");
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
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            final int scaleFactor = scaledResolution.getScaleFactor();
            final float i = 1 / ((float) scaleFactor);
            GlStateManager.scale(i, i, i);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(tex);
            GlStateManager.color(1, 1, 1, 1);
            float aspectRatio = width / (float) height;
            float scaleWidth = scaledResolution.getScaledWidth() * scaleFactor;

            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) scaleWidth *= PatcherConfig.imagePreviewWidth;
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) scaleWidth = this.width;

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
            connection.setInstanceFollowRedirects(true);
            connection.addRequestProperty("User-Agent", "Patcher Image Previewer");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            try (InputStream stream = connection.getInputStream()) {
                if (connection.getHeaderField("Content-Type").contains("text/html")) {
                    String body = IOUtils.toString(stream);
                    String imageURL = "";
                    Matcher matcher;
                    if ((matcher = OGP_IMAGE_REGEX.matcher(body)).find()) {
                        imageURL = matcher.group("url");
                    } else if ((matcher = IMG_TAG_REGEX.matcher(body)).find()) {
                        imageURL = matcher.group("url");
                    }
                    if (imageURL.startsWith("/")) {
                        URL urlObj = new URL(url);
                        imageURL = urlObj.getProtocol() + "://" + urlObj.getHost() + imageURL;
                    }

                    if (!imageURL.isEmpty()) {
                        loadUrl(imageURL);
                        connection.disconnect();
                        return;
                    }
                }

                image = TextureUtil.readBufferedImage(stream);
            }
        } catch (Exception e) {
            Patcher.instance.getLogger().error("Failed to load an image preview from {}", url, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
