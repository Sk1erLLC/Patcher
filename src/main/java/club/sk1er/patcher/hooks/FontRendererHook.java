package club.sk1er.patcher.hooks;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.FontRendererAccessor;
import club.sk1er.patcher.mixins.accessors.GlStateManagerAccessor;
import club.sk1er.patcher.optifine.OptiFineFontRendererHandler;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.hash.StringHash;
import club.sk1er.patcher.util.enhancement.text.CachedString;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class FontRendererHook {

    public static boolean forceRefresh = false;
    public static final String characterDictionary = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private final EnhancedFontRenderer enhancedFontRenderer = EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class);
    private final FontRenderer fontRenderer;
    private final FontRendererAccessor fontRendererAccessor;
    private final Minecraft mc = Minecraft.getMinecraft();
    private OptiFineHook hook = new OptiFineHook();
    public int glTextureId = -1;
    private int texSheetDim = 256;
    private float fontTexHeight = 16 * texSheetDim + 128;
    private float fontTexWidth = 16 * texSheetDim;
    private int regularCharDim = 128;
    private boolean drawing = false;
    private static final String COLOR_RESET_PHRASE = '\u00a7' + "r";

    public FontRendererHook(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.fontRendererAccessor = (FontRendererAccessor) fontRenderer;
    }

    private void establishSize() {
        int regWidth = 256;
        for (int i = 0; i < 256; i++) {
            try (final InputStream stream = mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                regWidth = ImageIO.read(stream).getWidth();
                break;
            } catch (Exception ignored) {
            }
        }

        texSheetDim = regWidth;
        int specWidth = 128;

        try (final InputStream stream = mc.getResourceManager().getResource(fontRendererAccessor.getLocationFontTexture()).getInputStream()) {
            specWidth = ImageIO.read(stream).getWidth();
        } catch (IOException e) {
            Patcher.instance.getLogger().error("Failed to read font texture while establishing size.", e);
        }

        regularCharDim = specWidth;
        fontTexHeight = 16 * texSheetDim + specWidth;
        fontTexWidth = 16 * texSheetDim;
    }

    public void create() {
        establishSize();
        hook = new OptiFineHook();
        forceRefresh = false;

        if (glTextureId != -1) {
            GlStateManager.deleteTexture(glTextureId);
        }

        final BufferedImage bufferedImage = new BufferedImage((int) fontTexWidth, (int) fontTexHeight, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 256; i++) {
            try (final InputStream stream = mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                bufferedImage.getGraphics().drawImage(ImageIO.read(stream), i / 16 * texSheetDim, i % 16 * texSheetDim, null);
                if (i == 0x0d) {
                    try (final InputStream sustream = FontRendererHook.class.getResourceAsStream("/assets/patcher/font_glyph_data.bin")) {
                        Graphics2D graphics = bufferedImage.createGraphics();
                        graphics.setComposite(AlphaComposite.Src);
                        graphics.drawImage(ImageIO.read(Objects.requireNonNull(sustream)), 0x9DE % 16 * 16 /* 224 */, (i % 16 * texSheetDim) + (0x9DE / 16 - 16 /* 141 */), null);
                        fontRendererAccessor.getGlyphWidth()[0xD9E] = 14;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        try (final InputStream stream = mc.getResourceManager().getResource(fontRendererAccessor.getLocationFontTexture()).getInputStream()) {
            bufferedImage.getGraphics().drawImage(ImageIO.read(stream), 0, 16 * texSheetDim, null);
        } catch (IOException e) {
            Patcher.instance.getLogger().error("Failed to draw texture sheet.", e);
        }

        glTextureId = new DynamicTexture(bufferedImage).getGlTextureId();
    }

    private void deleteTextureId() {
        if (glTextureId != -1) {
            GlStateManager.deleteTexture(glTextureId);
            glTextureId = -1;
        }
    }

    public static String clearColorReset(String text) {
        int startIndex = 0;
        int endIndex = text.length();

        while (text.indexOf(COLOR_RESET_PHRASE, startIndex) == startIndex) {
            startIndex += 2;
        }
        int e;
        while ((e = text.lastIndexOf(COLOR_RESET_PHRASE, endIndex - 1)) == endIndex - 2 && e != -1) {
            endIndex -= 2;
        }

        if (endIndex < startIndex) endIndex = startIndex;

        return text.substring(startIndex, endIndex);
    }

    @SuppressWarnings({"unused"})
    public boolean renderStringAtPos(String text, boolean shadow) {
        if (this.fontRendererAccessor.getRenderEngine() == null || !PatcherConfig.optimizedFontRenderer) {
            this.deleteTextureId();
            return false;
        }

        if (glTextureId == -1 || forceRefresh) {
            create();
        }

        text = clearColorReset(text);

        if (text.isEmpty()) {
            return false;
        }

        float posX = this.fontRendererAccessor.getPosX();
        float posY = this.fontRendererAccessor.getPosY();
        this.fontRendererAccessor.setPosY(0.0f);
        this.fontRendererAccessor.setPosX(0.0f);

        float red = this.fontRendererAccessor.getRed();
        float green = this.fontRendererAccessor.getGreen();
        float blue = this.fontRendererAccessor.getBlue();
        float alpha = this.fontRendererAccessor.getAlpha();

        GlStateManager.bindTexture(glTextureId);
        GlStateManager.translate(posX, posY, 0F);

        final GlStateManager.TextureState[] textureStates = GlStateManagerAccessor.getTextureState();
        final GlStateManager.TextureState textureState = textureStates[GlStateManagerAccessor.getActiveTextureUnit()];

        final StringHash hash = new StringHash(text, red, green, blue, alpha, shadow);
        final CachedString cachedString = PatcherConfig.cacheFontData ? this.enhancedFontRenderer.get(hash) : null;

        if (cachedString != null) {
            GlStateManager.color(red, green, blue, alpha);
            GlStateManager.callList(cachedString.getListId());

            // Call so states in game know the texture was changed.
            // Otherwise, the game won't know the active texture was changed on the GPU
            textureState.textureName = glTextureId;

            // Save thing as texture, it updated in GL, so we need to update the MC cache of that value
            GlStateManager.Color colorState = GlStateManagerAccessor.getColorState();
            colorState.red = cachedString.getLastRed();
            colorState.green = cachedString.getLastGreen();
            colorState.blue = cachedString.getLastBlue();
            colorState.alpha = cachedString.getLastAlpha();
            GlStateManager.translate(-posX, -posY, 0.0f);
            GlStateManager.resetColor();
            GlStateManager.color(1, 1, 1, 1);

            this.fontRendererAccessor.setPosX(posX + cachedString.getWidth());
            this.fontRendererAccessor.setPosY(posY + cachedString.getHeight());
            return true;
        }

        int list = 0;
        textureState.textureName = glTextureId;
        GlStateManager.resetColor();
        if (PatcherConfig.cacheFontData) {
            list = enhancedFontRenderer.getGlList();
            GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE);
        }

        boolean obfuscated = false;
        final CachedString value = new CachedString(text, list, this.fontRendererAccessor.getPosX() - posX, this.fontRendererAccessor.getPosY() - posY);
        final Deque<RenderPair> underline = new LinkedList<>();
        final Deque<RenderPair> strikethrough = new LinkedList<>();

        value.setLastRed(red);
        value.setLastGreen(green);
        value.setLastBlue(blue);
        value.setLastAlpha(alpha);

        for (int messageChar = 0; messageChar < text.length(); ++messageChar) {
            char letter = text.charAt(messageChar);

            if (letter == 167 && messageChar + 1 < text.length()) {
                int styleIndex = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(messageChar + 1));

                if (styleIndex < 16) {
                    this.fontRendererAccessor.setStrikethroughStyle(false);
                    this.fontRendererAccessor.setUnderlineStyle(false);
                    this.fontRendererAccessor.setItalicStyle(false);
                    this.fontRendererAccessor.setRandomStyle(false);
                    this.fontRendererAccessor.setBoldStyle(false);

                    if (styleIndex < 0) {
                        styleIndex = 15;
                    }

                    if (shadow) {
                        styleIndex += 16;
                    }

                    int currentColor = fontRendererAccessor.getColorCode()[styleIndex];
                    currentColor = OptiFineFontRendererHandler.getTextColor(styleIndex, currentColor);
                    this.fontRendererAccessor.setTextColor(currentColor);

                    final float colorRed = (float) (currentColor >> 16) / 255.0F;
                    final float colorGreen = (float) (currentColor >> 8 & 255) / 255.0F;
                    final float colorBlue = (float) (currentColor & 255) / 255.0F;

                    GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

                    value.setLastAlpha(alpha);
                    value.setLastGreen(colorGreen);
                    value.setLastBlue(colorBlue);
                    value.setLastRed(colorRed);
                } else if (styleIndex == 16) {
                    this.fontRendererAccessor.setRandomStyle(true);
                    obfuscated = true;
                } else if (styleIndex == 17) {
                    this.fontRendererAccessor.setBoldStyle(true);
                } else if (styleIndex == 18) {
                    this.fontRendererAccessor.setStrikethroughStyle(true);
                } else if (styleIndex == 19) {
                    this.fontRendererAccessor.setUnderlineStyle(true);
                } else if (styleIndex == 20) {
                    this.fontRendererAccessor.setItalicStyle(true);
                } else {
                    this.fontRendererAccessor.setRandomStyle(false);
                    this.fontRendererAccessor.setBoldStyle(false);
                    this.fontRendererAccessor.setStrikethroughStyle(false);
                    this.fontRendererAccessor.setUnderlineStyle(false);
                    this.fontRendererAccessor.setItalicStyle(false);
                    GlStateManager.color(red, green, blue, alpha);

                    value.setLastGreen(green);
                    value.setLastAlpha(alpha);
                    value.setLastBlue(blue);
                    value.setLastRed(red);
                }

                ++messageChar;
            } else {
                int index = characterDictionary.indexOf(letter);

                if (this.fontRendererAccessor.isRandomStyle() && index != -1) {
                    final float charWidthFloat = getCharWidthFloat(letter);
                    char charIndex;

                    do {
                        index = this.fontRenderer.fontRandom.nextInt(characterDictionary.length());
                        charIndex = characterDictionary.charAt(index);
                    } while (charWidthFloat != getCharWidthFloat(charIndex));

                    letter = charIndex;
                }

                final boolean unicode = this.fontRenderer.getUnicodeFlag();
                final float boldWidth = getBoldOffset(index);
                final boolean small = (letter == 0 || index == -1 || unicode) && shadow;

                if (small) {
                    this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() - boldWidth);
                    this.fontRendererAccessor.setPosY(fontRendererAccessor.getPosY() - boldWidth);
                }

                float effectiveWidth = this.renderChar(letter, this.fontRendererAccessor.isItalicStyle());
                if (small) {
                    this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() + boldWidth);
                    this.fontRendererAccessor.setPosY(fontRendererAccessor.getPosY() + boldWidth);
                }

                if (this.fontRendererAccessor.isBoldStyle()) {
                    this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() + boldWidth);

                    if (small) {
                        this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() - boldWidth);
                        this.fontRendererAccessor.setPosY(fontRendererAccessor.getPosY() - boldWidth);
                    }

                    this.renderChar(letter, this.fontRendererAccessor.isItalicStyle());
                    this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() -boldWidth);

                    if (small) {
                        this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() + boldWidth);
                        this.fontRendererAccessor.setPosY(fontRendererAccessor.getPosY() + boldWidth);
                    }

                    ++effectiveWidth;
                }

                if (this.fontRendererAccessor.isStrikethroughStyle()) {
                    adjustOrAppend(strikethrough, this.fontRendererAccessor.getPosX(), effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }

                if (this.fontRendererAccessor.isUnderlineStyle()) {
                    adjustOrAppend(underline, this.fontRendererAccessor.getPosX(), effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }

                // Intentional cast to int to round down, see FontRenderer#doDraw
                this.fontRendererAccessor.setPosX(fontRendererAccessor.getPosX() + (int) effectiveWidth);
            }
        }

        endDrawing();
        final boolean hasStyle = underline.size() > 0 || strikethrough.size() > 0;

        if (hasStyle) {
            GlStateManager.disableTexture2D();
            GL11.glBegin(GL11.GL_QUADS);

            for (final RenderPair renderPair : strikethrough) {
                GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
                GL11.glVertex2f(renderPair.posX, this.fontRendererAccessor.getPosY() + 4.0f);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 4.0f);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 3.0f);
                GL11.glVertex2f(renderPair.posX, this.fontRendererAccessor.getPosY() + 3.0f);
            }

            for (final RenderPair renderPair : underline) {
                GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
                GL11.glVertex2f(renderPair.posX - 1.0f, this.fontRendererAccessor.getPosY() + 9);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 9);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 9 - 1.0F);
                GL11.glVertex2f(renderPair.posX - 1.0f, this.fontRendererAccessor.getPosY() + 9 - 1.0F);
            }

            GL11.glEnd();
            GlStateManager.enableTexture2D();
        }

        if (PatcherConfig.cacheFontData) {
            GL11.glEndList();
            this.enhancedFontRenderer.cache(hash, value);
        }

        value.setWidth(this.fontRendererAccessor.getPosX());

        this.fontRendererAccessor.setPosY(posY + value.getHeight());
        this.fontRendererAccessor.setPosX(posX + value.getWidth());

        if (obfuscated) {
            this.enhancedFontRenderer.getObfuscated().add(hash);
        }

        GlStateManager.translate(-posX, -posY, 0F);
        return true;
    }

    private void adjustOrAppend(Deque<RenderPair> style, float posX, float effectiveWidth, float lastRed, float lastGreen, float lastBlue, float lastAlpha) {
        final RenderPair lastStart = style.peekLast();
        if (lastStart != null && lastStart.red == lastRed && lastStart.green == lastGreen && lastStart.blue == lastBlue && lastStart.alpha == lastAlpha) {
            if (lastStart.posX + lastStart.width >= posX - 1) {
                lastStart.width = posX + effectiveWidth - lastStart.posX;
                return;
            }
        }
        style.add(new RenderPair(posX, effectiveWidth, lastRed, lastGreen, lastBlue, lastAlpha));
    }

    private float getBoldOffset(int index) {
        return index == -1 || fontRenderer.getUnicodeFlag() ? 0.5F : getOptifineBoldOffset();
    }

    private float getOptifineBoldOffset() {
        return hook.getOptifineBoldOffset(fontRenderer);
    }

    public float renderChar(char ch, boolean italic) {
        if (ch == 32 || ch == 160) {
            return fontRenderer.getUnicodeFlag() ? 4.0F : getCharWidthFloat(ch);
        } else {
            final int charIndex = characterDictionary.indexOf(ch);
            return charIndex != -1 && !this.fontRenderer.getUnicodeFlag() ? this.renderDefaultChar(charIndex, italic, ch) : this.renderUnicodeChar(ch, italic);
        }
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    private float renderDefaultChar(int characterIndex, boolean italic, char ch) {
        final float characterX = (characterIndex % 16 * 8 * regularCharDim >> 7) + .01f;
        final float characterY = ((characterIndex >> 4) * 8 * regularCharDim >> 7) + 16 * texSheetDim + .01f;

        final int italicStyle = italic ? 1 : 0;
        final float charWidth = getCharWidthFloat(ch);
        final float smallCharWidth = charWidth - 0.01F;

        startDrawing();
        final float uvHeight = 7.99F * regularCharDim / 128;
        final float uvWidth = smallCharWidth * regularCharDim / 128;

        GL11.glTexCoord2f(characterX / fontTexWidth, characterY / fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + (float) italicStyle, this.fontRendererAccessor.getPosY());

        GL11.glTexCoord2f(characterX / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() - (float) italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        final int offset = regularCharDim / 128;
        GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + smallCharWidth - 1.0F - (float) italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, characterY / fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + smallCharWidth - 1.0F + (float) italicStyle, this.fontRendererAccessor.getPosY());

        return charWidth;
    }

    private void startDrawing() {
        if (!drawing) {
            drawing = true;
            GL11.glBegin(GL11.GL_QUADS);
        }
    }

    private void endDrawing() {
        if (drawing) {
            drawing = false;
            GL11.glEnd();
        }
    }

    private Pair<Float, Float> getUV(char characterIndex) {
        final int page = characterIndex / 256;
        final int row = page >> 4;
        final int column = page % 16;
        final int glyphWidth = this.fontRendererAccessor.getGlyphWidth()[characterIndex] >>> 4;
        final float charX = (float) (characterIndex % 16 << 4) + glyphWidth + (.05f * page / 39f);
        final float charY = (float) (((characterIndex & 255) >> 4) * 16) + (.05f * page / 39f);
        return new Pair<>((row * texSheetDim + charX) / fontTexWidth, (column * texSheetDim + charY) / fontTexHeight); //16 rows each with a size of 64px
    }

    /**
     * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
     */
    private float renderUnicodeChar(char ch, boolean italic) {
        if (this.fontRendererAccessor.getGlyphWidth()[ch] == 0) {
            return 0.0F;
        } else {
            final Pair<Float, Float> uv = getUV(ch);
            final int glyphX = this.fontRendererAccessor.getGlyphWidth()[ch] >>> 4;
            final int glyphY = this.fontRendererAccessor.getGlyphWidth()[ch] & 15;
            final float floatGlyphX = (float) glyphX;
            final float modifiedY = (float) glyphY + 1;
            final float combinedGlyphSize = modifiedY - floatGlyphX - 0.02F;
            final float italicStyle = italic ? 1.0F : 0.0F;
            startDrawing();

            final float v = 15.98F * texSheetDim / 256;
            GL11.glTexCoord2f(uv.component1(), uv.component2());
            GL11.glVertex2f(this.fontRendererAccessor.getPosX() + italicStyle, this.fontRendererAccessor.getPosY());

            GL11.glTexCoord2f(uv.component1(), uv.component2() + v / fontTexHeight);
            GL11.glVertex2f(this.fontRendererAccessor.getPosX() - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

            final float texAdj = combinedGlyphSize + .5f;
            GL11.glTexCoord2f(uv.component1() + texAdj / fontTexHeight, uv.component2() + v / fontTexHeight);
            GL11.glVertex2f(this.fontRendererAccessor.getPosX() + combinedGlyphSize / 2.0F - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

            GL11.glTexCoord2f(uv.component1() + texAdj / fontTexHeight, uv.component2());
            GL11.glVertex2f(this.fontRendererAccessor.getPosX() + combinedGlyphSize / 2.0F + italicStyle, this.fontRendererAccessor.getPosY());

            return (modifiedY - floatGlyphX) / 2.0F + 1.0F;
        }
    }

    private float getCharWidthFloat(char c) { //Remapped to optifine's stuff if needed
        return hook.getCharWidth(fontRenderer, c);
    }

    @SuppressWarnings("unused")
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }

        Map<String, Integer> stringWidthCache = enhancedFontRenderer.getStringWidthCache();
        if (stringWidthCache.size() > 5000) {
            stringWidthCache.clear();
        }

        return stringWidthCache.computeIfAbsent(text, width -> getUncachedWidth(text));
    }

    private int getUncachedWidth(String text) {
        if (text == null) {
            return 0;
        } else {
            float width = 0;
            boolean bold = false;

            for (int messageChar = 0; messageChar < text.length(); ++messageChar) {
                char character = text.charAt(messageChar);
                float characterWidth = getCharWidthFloat(character);

                if (characterWidth < 0 && messageChar < text.length() - 1) {
                    ++messageChar;
                    character = text.charAt(messageChar);

                    if (character != 108 && character != 76) {
                        if (character == 114 || character == 82) {
                            bold = false;
                        }
                    } else {
                        bold = true;
                    }

                    characterWidth = 0;
                }

                width += characterWidth;

                if (bold && characterWidth > 0) {
                    width += getOptifineBoldOffset();
                }
            }

            return (int) width;
        }
    }

    public EnhancedFontRenderer getEnhancedFontRenderer() {
        return enhancedFontRenderer;
    }

    static class RenderPair {
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        float posX;
        float width;

        public RenderPair(float posX, float width, float red, float green, float blue, float alpha) {
            this.posX = posX;
            this.width = width;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }
}
