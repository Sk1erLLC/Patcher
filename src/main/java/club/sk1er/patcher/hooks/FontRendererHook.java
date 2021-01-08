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

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.text.CachedString;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import club.sk1er.patcher.util.hash.StringHash;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public final class FontRendererHook {

    public static boolean forceRefresh = false;
    private final EnhancedFontRenderer enhancedFontRenderer = EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class);
    private final FontRenderer fontRenderer;
    private final String characterDictionary = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private final Minecraft mc = Minecraft.getMinecraft();
    public int GL_TEX = -1;
    private OptifineHook hook = new OptifineHook();
    private int texSheetDim = 256;
    private float fontTexHeight = 16 * texSheetDim + 128;
    private float fontTexWidth = 16 * texSheetDim;
    private int regularCharDim = 128;
    private boolean drawing = false;

    public FontRendererHook(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    private void establishSize() {
        int regWidth = 256;
        for (int i = 0; i < 256; i++) {
            try (InputStream stream = mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                regWidth = ImageIO.read(stream).getWidth();
                break;
            } catch (Exception ignored) {
            }
        }

        texSheetDim = regWidth;
        int specWidth = 128;

        try (InputStream stream = mc.getResourceManager().getResource(fontRenderer.locationFontTexture).getInputStream()) {
            specWidth = ImageIO.read(stream).getWidth();
        } catch (IOException e) {
            e.printStackTrace();
        }

        regularCharDim = specWidth;
        fontTexHeight = 16 * texSheetDim + specWidth;
        fontTexWidth = 16 * texSheetDim;
    }

    private void create() {
        establishSize();
        hook = new OptifineHook();
        forceRefresh = false;

        if (GL_TEX != -1) {
            GlStateManager.deleteTexture(GL_TEX);
        }

        final BufferedImage bufferedImage = new BufferedImage((int) fontTexWidth, (int) fontTexHeight, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 256; i++) {
            try (InputStream stream = mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                bufferedImage.getGraphics().drawImage(ImageIO.read(stream), i / 16 * texSheetDim, i % 16 * texSheetDim, null);
            } catch (Exception ignored) {
            }
        }

        try (InputStream stream = mc.getResourceManager().getResource(fontRenderer.locationFontTexture).getInputStream()) {
            bufferedImage.getGraphics().drawImage(ImageIO.read(stream), 0, 16 * texSheetDim, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GL_TEX = new DynamicTexture(bufferedImage).getGlTextureId();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public boolean renderStringAtPos(String text, boolean shadow) {
        if (this.fontRenderer.renderEngine == null || !PatcherConfig.optimizedFontRenderer) {
            if (GL_TEX != -1) {
                GlStateManager.deleteTexture(GL_TEX);
                GL_TEX = -1;
            }

            return false;
        }

        if (GL_TEX == -1 || forceRefresh) {
            create();
        }

        fontRenderer.randomStyle = false;
        fontRenderer.boldStyle = false;
        fontRenderer.italicStyle = false;
        fontRenderer.underlineStyle = false;
        fontRenderer.strikethroughStyle = false;

        while (text.startsWith('\u00a7' + "r")) {
            text = text.substring(2);
        }

        while (text.endsWith('\u00a7' + "r")) {
            text = text.substring(0, text.length() - 2);
        }

        int list = 0;

        float posX = this.fontRenderer.posX;
        float posY = this.fontRenderer.posY;
        this.fontRenderer.posY = 0.0f;
        this.fontRenderer.posX = 0.0f;

        float red = this.fontRenderer.red;
        float green = this.fontRenderer.green;
        float blue = this.fontRenderer.blue;
        float alpha = this.fontRenderer.alpha;

        StringHash hash = new StringHash(text, red, green, blue, alpha, shadow);
        GlStateManager.bindTexture(GL_TEX);
        GlStateManager.translate(posX, posY, 0F);

        GlStateManager.TextureState[] textureStates = GlStateManager.textureState;
        int activeTextureUnit = GlStateManager.activeTextureUnit;

        GlStateManager.TextureState textureState = textureStates[activeTextureUnit];
        final boolean cacheFontData = PatcherConfig.cacheFontData;
        CachedString cachedString = cacheFontData ? this.enhancedFontRenderer.get(hash) : null;

        if (cachedString != null) {
            GlStateManager.color(red, blue, green, alpha);
            GlStateManager.callList(cachedString.getListId());

            // Call so states in game know the texture was changed.
            // Otherwise the game won't know the active texture was changed on the GPU
            textureState.textureName = GL_TEX;

            // Save thing as texture, it updated in GL so we need to update the MC cache of that value
            GlStateManager.Color colorState = GlStateManager.colorState;
            colorState.red = cachedString.getLastRed();
            colorState.green = cachedString.getLastGreen();
            colorState.blue = cachedString.getLastBlue();
            colorState.alpha = cachedString.getLastAlpha();
            GlStateManager.translate(-posX, -posY, 0.0f);
            GlStateManager.color(1, 1, 1, 1);

            this.fontRenderer.posX = posX + cachedString.getWidth();
            this.fontRenderer.posY = posY + cachedString.getHeight();
            return true;
        }

        textureState.textureName = GL_TEX;
        GlStateManager.resetColor();
        if (cacheFontData) {
            list = enhancedFontRenderer.getGlList();
            GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE);
        }

        boolean obfuscated = false;
        CachedString value = new CachedString(text, list, this.fontRenderer.posX - posX, this.fontRenderer.posY - posY);

        int[] colorCode = this.fontRenderer.colorCode;
        Deque<RenderPair> underline = new LinkedList<>();
        Deque<RenderPair> strikeThough = new LinkedList<>();

        for (int messageChar = 0; messageChar < text.length(); ++messageChar) {
            char letter = text.charAt(messageChar);

            if (letter == 167 && messageChar + 1 < text.length()) {
                int styleIndex = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(messageChar + 1));

                if (styleIndex < 16) {
                    this.fontRenderer.strikethroughStyle = false;
                    this.fontRenderer.underlineStyle = false;
                    this.fontRenderer.italicStyle = false;
                    this.fontRenderer.randomStyle = false;
                    this.fontRenderer.boldStyle = false;

                    if (styleIndex < 0) {
                        styleIndex = 15;
                    }

                    if (shadow) {
                        styleIndex += 16;
                    }

                    int j1 = colorCode[styleIndex];
                    this.fontRenderer.textColor = j1;
                    float colorRed = (float) (j1 >> 16) / 255.0F;
                    float colorGreen = (float) (j1 >> 8 & 255) / 255.0F;
                    float colorBlue = (float) (j1 & 255) / 255.0F;
                    GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

                    value.setLastAlpha(alpha);
                    value.setLastGreen(colorGreen);
                    value.setLastBlue(colorBlue);
                    value.setLastRed(colorRed);
                } else if (styleIndex == 16) {
                    this.fontRenderer.randomStyle = true;
                    obfuscated = true;
                } else if (styleIndex == 17) {
                    this.fontRenderer.boldStyle = true;
                } else if (styleIndex == 18) {
                    this.fontRenderer.strikethroughStyle = true;
                } else if (styleIndex == 19) {
                    this.fontRenderer.underlineStyle = true;
                } else if (styleIndex == 20) {
                    this.fontRenderer.italicStyle = true;
                } else {
                    this.fontRenderer.strikethroughStyle = this.fontRenderer.underlineStyle = false;
                    this.fontRenderer.italicStyle = this.fontRenderer.randomStyle = false;
                    this.fontRenderer.boldStyle = false;
                    GlStateManager.color(red, blue, green, alpha);

                    value.setLastGreen(green);
                    value.setLastAlpha(alpha);
                    value.setLastBlue(blue);
                    value.setLastRed(red);
                }

                ++messageChar;
            } else {
                int obfuscationIndex = shadow || this.fontRenderer.randomStyle ? characterDictionary.indexOf(letter) : 0; //save calculation

                if (this.fontRenderer.randomStyle && obfuscationIndex != -1) {
                    float charWidthFloat = getCharWidthFloat(letter);
                    char charIndex;

                    do {
                        obfuscationIndex = this.fontRenderer.fontRandom.nextInt(characterDictionary.length());
                        charIndex = characterDictionary.charAt(obfuscationIndex);
                    } while (charWidthFloat != getCharWidthFloat(charIndex));

                    letter = charIndex;
                }

                boolean unicode = this.fontRenderer.unicodeFlag;
                float boldWidth = getBoldOffset(obfuscationIndex);
                boolean small = (letter == 0 || obfuscationIndex == -1 || unicode) && shadow;

                if (small) {
                    this.fontRenderer.posX -= boldWidth;
                    this.fontRenderer.posY -= boldWidth;
                }

                float effectiveWidth = this.renderChar(letter, this.fontRenderer.italicStyle);
                if (small) {
                    this.fontRenderer.posX += boldWidth;
                    this.fontRenderer.posY += boldWidth;
                }

                if (this.fontRenderer.boldStyle) {
                    this.fontRenderer.posX += boldWidth;

                    if (small) {
                        this.fontRenderer.posX -= boldWidth;
                        this.fontRenderer.posY -= boldWidth;
                    }

                    this.renderChar(letter, this.fontRenderer.italicStyle);
                    this.fontRenderer.posX -= boldWidth;

                    if (small) {
                        this.fontRenderer.posX += boldWidth;
                        this.fontRenderer.posY += boldWidth;
                    }
                    effectiveWidth += boldWidth;
                }

                if (this.fontRenderer.strikethroughStyle) {
                    adjustOrAppend(strikeThough, this.fontRenderer.posX, effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }

                if (this.fontRenderer.underlineStyle) {
                    adjustOrAppend(underline, this.fontRenderer.posX, effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }

                this.fontRenderer.posX += effectiveWidth;
            }
        }

        endDrawing();
        boolean hasStyle = underline.size() > 0 || strikeThough.size() > 0;

        if (hasStyle) {
            GlStateManager.disableTexture2D();
            GL11.glBegin(GL11.GL_QUADS);
        }

        for (RenderPair renderPair : strikeThough) {
            GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
            GL11.glVertex2f(renderPair.posX, this.fontRenderer.posY + 4.0f);
            GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRenderer.posY + 4.0f);
            GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRenderer.posY + 3.0f);
            GL11.glVertex2f(renderPair.posX, this.fontRenderer.posY + 3.0f);
        }

        for (RenderPair renderPair : underline) {
            GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
            final float fontHeight = this.fontRenderer.FONT_HEIGHT;
            GL11.glVertex2f(renderPair.posX - 1.0f, this.fontRenderer.posY + fontHeight);
            GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRenderer.posY + fontHeight);
            GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRenderer.posY + fontHeight - 1.0F);
            GL11.glVertex2f(renderPair.posX - 1.0f, this.fontRenderer.posY + fontHeight - 1.0F);
        }

        if (hasStyle) {
            GL11.glEnd();
        }

        GlStateManager.enableTexture2D();

        if (cacheFontData) {
            GL11.glEndList();
            this.enhancedFontRenderer.cache(hash, value);
        }

        value.setWidth(this.fontRenderer.posX);

        this.fontRenderer.posY = posY + value.getHeight();
        this.fontRenderer.posX = posX + value.getWidth();

        if (obfuscated) {
            this.enhancedFontRenderer.getObfuscated().add(hash);
        }

        GlStateManager.translate(-posX, -posY, 0F);
        return true;
    }

    private void adjustOrAppend(Deque<RenderPair> underline, float posX, float effectiveWidth, float lastRed, float lastGreen, float lastBlue, float lastAlpha) {
        RenderPair lastStart = underline.peekLast();
        if (lastStart != null && lastStart.red == lastRed && lastStart.green == lastGreen && lastStart.blue == lastBlue && lastStart.alpha == lastAlpha) {
            if (lastStart.posX + lastStart.width >= posX - 1) {
                lastStart.width = posX + effectiveWidth - lastStart.posX;
            }
        } else {
            underline.add(new RenderPair(posX, effectiveWidth, lastRed, lastGreen, lastBlue, lastAlpha));
        }
    }


    private float getBoldOffset(int j) {
        return fontRenderer.unicodeFlag || j == -1 ? 0.5F : getOptifineBoldOffset();
    }

    private float getOptifineBoldOffset() {
        return hook.getOptifineBoldOffset(fontRenderer);
    }

    public float renderChar(char ch, boolean italic) {
        if (ch == 32 || ch == 160) {
            return fontRenderer.unicodeFlag ? 4.0F : getCharWidthFloat(ch);
        } else {
            int charIndex = characterDictionary.indexOf(ch);
            return charIndex != -1 && !this.fontRenderer.unicodeFlag ? this.renderDefaultChar(charIndex, italic, ch) : this.renderUnicodeChar(ch, italic);
        }
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    protected float renderDefaultChar(int characterIndex, boolean italic, char ch) {
        float characterX = (characterIndex % 16 * 8 * regularCharDim >> 7) + .01f;
        float characterY = ((characterIndex >> 4) * 8 * regularCharDim >> 7) + 16 * texSheetDim + .01f;

        int italicStyle = italic ? 1 : 0;
        float charWidth = getCharWidthFloat(ch);
        float smallCharWidth = charWidth - 0.01F;

        startDrawing();
        float uvHeight = 7.99F * regularCharDim / 128;
        float uvWidth = smallCharWidth * regularCharDim / 128;

        GL11.glTexCoord2f(characterX / fontTexWidth, characterY / fontTexHeight);
        GL11.glVertex2f(this.fontRenderer.posX + (float) italicStyle, this.fontRenderer.posY);

        GL11.glTexCoord2f(characterX / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
        GL11.glVertex2f(this.fontRenderer.posX - (float) italicStyle, this.fontRenderer.posY + 7.99F);

        final int offset = regularCharDim / 128;
        GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
        GL11.glVertex2f(this.fontRenderer.posX + smallCharWidth - 1.0F - (float) italicStyle, this.fontRenderer.posY + 7.99F);

        GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, characterY / fontTexHeight);
        GL11.glVertex2f(this.fontRenderer.posX + smallCharWidth - 1.0F + (float) italicStyle, this.fontRenderer.posY);

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
        final int row = page / 16;
        final int column = page % 16;
        final int glyphWidth = this.fontRenderer.glyphWidth[characterIndex] >>> 4;
        final float charX = (float) (characterIndex % 16 << 4) + glyphWidth + (.05f * page / 39f);
        final float charY = (float) (((characterIndex & 255) >> 4) * 16) + (.05f * page / 39f);
        return new Pair<>((row * texSheetDim + charX) / fontTexWidth, (column * texSheetDim + charY) / fontTexHeight); //16 rows each with a size of 64px
    }

    /**
     * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
     */
    protected float renderUnicodeChar(char ch, boolean italic) {
        if (this.fontRenderer.glyphWidth[ch] == 0) {
            return 0.0F;
        } else {
            final Pair<Float, Float> uv = getUV(ch);
            final int glyphX = this.fontRenderer.glyphWidth[ch] >>> 4;
            final int glyphY = this.fontRenderer.glyphWidth[ch] & 15;
            final float floatGlyphX = (float) glyphX;
            final float modifiedY = (float) (glyphY + 1);
            final float combinedGlyphSize = modifiedY - floatGlyphX - 0.02F;
            final float italicStyle = italic ? 1.0F : 0.0F;
            startDrawing();

            final float v = 15.98F * texSheetDim / 256;
            GL11.glTexCoord2f(uv.component1(), uv.component2());
            GL11.glVertex2f(this.fontRenderer.posX + italicStyle, this.fontRenderer.posY);

            GL11.glTexCoord2f(uv.component1(), uv.component2() + v / fontTexHeight);
            GL11.glVertex2f(this.fontRenderer.posX - italicStyle, this.fontRenderer.posY + 7.99F);

            final float texAdj = combinedGlyphSize + .5f;
            GL11.glTexCoord2f(uv.component1() + texAdj / fontTexHeight, uv.component2() + v / fontTexHeight);
            GL11.glVertex2f(this.fontRenderer.posX + combinedGlyphSize / 2.0F - italicStyle, this.fontRenderer.posY + 7.99F);

            GL11.glTexCoord2f(uv.component1() + texAdj / fontTexHeight, uv.component2());
            GL11.glVertex2f(this.fontRenderer.posX + combinedGlyphSize / 2.0F + italicStyle, this.fontRenderer.posY);

            return (modifiedY - floatGlyphX) / 2.0F + 1.0F;
        }
    }

    private float getCharWidthFloat(char c) { //Remapped to optifine's stuff if needed
        return hook.getCharWidth(fontRenderer, c);
    }

    public int getStringWidth(String text) {
        Map<String, Integer> stringWidthCache = enhancedFontRenderer.getStringWidthCache();

        if (!PatcherConfig.optimizedFontRenderer) {
            if (stringWidthCache.size() != 0)
                stringWidthCache.clear();
            return getUncachedWidth(text);
        }

        if (text == null) {
            return 0;
        }

        if (stringWidthCache.size() > 5000) {
            stringWidthCache.clear();
        }

        return stringWidthCache.computeIfAbsent(text, width -> getUncachedWidth(text));
    }

    private int getUncachedWidth(String text) {
        if (text == null) {
            return 0;
        } else {
            float i = 0;
            boolean flag = false;

            for (int j = 0; j < text.length(); ++j) {
                char c0 = text.charAt(j);
                float k = getCharWidthFloat(c0);

                if (k < 0 && j < text.length() - 1) {
                    ++j;
                    c0 = text.charAt(j);

                    if (c0 != 108 && c0 != 76) {
                        if (c0 == 114 || c0 == 82) {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0) {
                    i += getBoldOffset(characterDictionary.indexOf(c0));
                }
            }

            return (int) i;
        }
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
