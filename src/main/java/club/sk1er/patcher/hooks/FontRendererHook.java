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
import club.sk1er.patcher.util.cache.CachedString;
import club.sk1er.patcher.util.enhancement.EnhancementManager;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import club.sk1er.patcher.util.hash.StringHash;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.minecraft.client.gui.FontRenderer.unicodePageLocations;

@SuppressWarnings("unused")
public class FontRendererHook {

    private final EnhancedFontRenderer enhancedFontRenderer = EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class);
    private final FontRenderer fontRenderer;
    private final String characterDictionary = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

    private boolean drawing;
    private int boundPage = -4;

    public FontRendererHook(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void renderStringAtPos(String text, boolean shadow) {
        while (text.startsWith('\u00a7' + "r")) {
            text = text.substring(2);
        }

        while (text.endsWith('\u00a7' + "r")) {
            text = text.substring(0, text.length() - 2);
        }

        int list = 0;

        float posX = fontRenderer.posX;
        float posY = fontRenderer.posY;
        fontRenderer.posX = 0;
        fontRenderer.posY = 0;

        float red = fontRenderer.red;
        float green = fontRenderer.green;
        float blue = fontRenderer.blue;
        float alpha = fontRenderer.alpha;

        StringHash hash = new StringHash(text, red, green, blue, alpha, shadow);
        GlStateManager.translate(posX, posY, 0.0F);

        if (PatcherConfig.optimizedFontRenderer) {
            GlStateManager.TextureState[] textureStates = GlStateManager.textureState;
            int activeTextureUnit = GlStateManager.activeTextureUnit;

            GlStateManager.TextureState textureState = textureStates[activeTextureUnit];
            CachedString cachedString = enhancedFontRenderer.get(hash);
            if (cachedString != null) {
                GlStateManager.color(red, blue, green, alpha);
                GlStateManager.callList(cachedString.getListId());

                textureState.textureName = cachedString.getLastTexture();

                GlStateManager.Color colorState = GlStateManager.colorState;
                colorState.red = cachedString.getLastRed();
                colorState.green = cachedString.getLastGreen();
                colorState.blue = cachedString.getLastBlue();
                colorState.alpha = cachedString.getLastAlpha();

                GlStateManager.translate(-posX, -posY, 0.0F);
                fontRenderer.posX = posX + cachedString.getWidth();
                fontRenderer.posY = posY + cachedString.getHeight();
                return;
            }

            textureState.textureName = -1;
            GlStateManager.resetColor();
            list = enhancedFontRenderer.getGlList();
            GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE);
        }

        boolean obfuscated = false;
        CachedString value = new CachedString(text, list, fontRenderer.posX - posX, fontRenderer.posY - posY);

        int[] colorCode = fontRenderer.colorCode;

        List<RenderPair> underline = new ArrayList<>();
        List<RenderPair> strikethrough = new ArrayList<>();

        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length()) {
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (index < 16) {
                    fontRenderer.strikethroughStyle = false;
                    fontRenderer.underlineStyle = false;
                    fontRenderer.italicStyle = false;
                    fontRenderer.randomStyle = false;
                    fontRenderer.boldStyle = false;

                    if (index < 0) {
                        index = 15;
                    }

                    if (shadow) {
                        index += 16;
                    }

                    int textColor = colorCode[index];
                    fontRenderer.textColor = textColor;

                    float colorRed = (float) (textColor >> 16) / 255.0F;
                    float colorGreen = (float) (textColor >> 8 & 255) / 255.0F;
                    float colorBlue = (float) (textColor & 255) / 255.0F;
                    GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

                    value.setLastAlpha(alpha);
                    value.setLastRed(colorRed);
                    value.setLastGreen(colorGreen);
                    value.setLastBlue(colorBlue);
                } else if (index == 16) {
                    fontRenderer.randomStyle = true;
                    obfuscated = true;
                } else if (index == 17) {
                    fontRenderer.boldStyle = true;
                } else if (index == 18) {
                    fontRenderer.strikethroughStyle = true;
                } else if (index == 19) {
                    fontRenderer.underlineStyle = true;
                } else if (index == 20) {
                    fontRenderer.italicStyle = true;
                } else {
                    fontRenderer.strikethroughStyle = fontRenderer.underlineStyle = false;
                    fontRenderer.italicStyle = fontRenderer.randomStyle = false;
                    fontRenderer.boldStyle = false;
                    GlStateManager.color(red, blue, green, alpha);

                    value.setLastAlpha(alpha);
                    value.setLastRed(red);
                    value.setLastBlue(blue);
                    value.setLastGreen(green);
                }

                ++i;
            } else {
                int j = shadow || fontRenderer.randomStyle ? characterDictionary.indexOf(c0) : 0;

                if (fontRenderer.randomStyle && j != -1) {
                    float charWidth = getCharWidthFloat(c0);
                    char c1;

                    do {
                        j = fontRenderer.fontRandom.nextInt(characterDictionary.length());
                        c1 = characterDictionary.charAt(j);
                    } while (charWidth != getCharWidthFloat(c1));

                    c0 = c1;
                }

                boolean unicode = fontRenderer.unicodeFlag;
                float unicodeCharWidth = unicode ? 0.5F : 1.0F;
                boolean flag = (c0 == 0 || j == -1 || unicode) && shadow;

                if (flag) {
                    fontRenderer.posX -= unicodeCharWidth;
                    fontRenderer.posY -= unicodeCharWidth;
                }

                float stringWidth = renderChar(c0, fontRenderer.italicStyle);

                if (flag) {
                    fontRenderer.posX += unicodeCharWidth;
                    fontRenderer.posY += unicodeCharWidth;
                }

                if (fontRenderer.boldStyle) {
                    fontRenderer.posX += unicodeCharWidth;

                    if (flag) {
                        fontRenderer.posX -= unicodeCharWidth;
                        fontRenderer.posY -= unicodeCharWidth;
                    }

                    renderChar(c0, fontRenderer.italicStyle);
                    fontRenderer.posX -= unicodeCharWidth;

                    if (flag) {
                        fontRenderer.posX += unicodeCharWidth;
                        fontRenderer.posY += unicodeCharWidth;
                    }

                    ++stringWidth;
                }

                if (fontRenderer.strikethroughStyle) {
                    strikethrough.add(new RenderPair(fontRenderer.posX, stringWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha()));
                }

                if (fontRenderer.underlineStyle) {
                    underline.add(new RenderPair(fontRenderer.posX, stringWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha()));
                }

                fontRenderer.posX += (int) stringWidth;
            }
        }

        endDrawing();
        boolean style = underline.size() > 0 || strikethrough.size() > 0;

        if (style) {
            GlStateManager.disableTexture2D();
            GL11.glBegin(GL11.GL_QUADS);
        }

        minify(strikethrough);
        minify(underline);

        for (RenderPair renderPair : strikethrough) {
            GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
            GL11.glVertex2f(renderPair.posX, fontRenderer.posY + 4.0f);
            GL11.glVertex2f(renderPair.posX + renderPair.width, fontRenderer.posY + 4.0f);
            GL11.glVertex2f(renderPair.posX + renderPair.width, fontRenderer.posY + 3.0f);
            GL11.glVertex2f(renderPair.posX, fontRenderer.posY + 3.0f);
        }

        for (RenderPair renderPair : underline) {
            GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
            GL11.glVertex2f(renderPair.posX - 1.0f, fontRenderer.posY + (float) fontRenderer.FONT_HEIGHT);
            GL11.glVertex2f(renderPair.posX + renderPair.width, fontRenderer.posY + (float) fontRenderer.FONT_HEIGHT);
            GL11.glVertex2f(renderPair.posX + renderPair.width, fontRenderer.posY + (float) fontRenderer.FONT_HEIGHT - 1.0F);
            GL11.glVertex2f(renderPair.posX - 1.0f, fontRenderer.posY + (float) fontRenderer.FONT_HEIGHT - 1.0F);
        }

        if (style) {
            GL11.glEnd();
        }

        GlStateManager.enableTexture2D();

        if (PatcherConfig.optimizedFontRenderer) {
            GL11.glEndList();
            enhancedFontRenderer.cache(hash, value);
        }

        value.setWidth(fontRenderer.posX);
        value.setLastTexture(GlStateManager.activeTextureUnit);

        fontRenderer.posX = posX + value.getWidth() + 1;
        fontRenderer.posY = posY + value.getHeight();

        if (obfuscated) {
            enhancedFontRenderer.getObfuscated().add(hash);
        }

        GlStateManager.translate(-posX, -posY, 0.0F);
    }

    private void minify(List<RenderPair> pairs) {
        Iterator<RenderPair> iterator = pairs.iterator();
        RenderPair lastStart = null;

        while (iterator.hasNext()) {
            RenderPair next = iterator.next();

            if (lastStart == null) {
                lastStart = next;
                continue;
            }

            if (lastStart.alpha == next.alpha && lastStart.red == next.red && lastStart.green == next.green && lastStart.blue == next.blue) {
                if (lastStart.posX + lastStart.width >= next.posX - 1) {
                    iterator.remove();
                    lastStart.width = next.posX + next.width - lastStart.posX;
                }
            } else {
                lastStart = next;
            }
        }
    }

    protected void bindTexture(ResourceLocation location) {
        try {
            FontRenderer fontRenderer = this.fontRenderer;
            TextureManager renderEngine = fontRenderer.renderEngine;
            if (renderEngine == null) {
                Field fontTexture = SplashProgress.class.getDeclaredField("fontTexture");
                fontTexture.setAccessible(true);
                Object o = fontTexture.get(null);
                Class<?> textureClass = o.getClass();
                Method bind = textureClass.getDeclaredMethod("bind");
                bind.setAccessible(true);
                bind.invoke(o);
            } else
                renderEngine.bindTexture(location);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Caught exception trying to bind texture. This happens on the splash screen occasionally.");
        }
    }

    private void endDrawing() {
        if (drawing) {
            drawing = false;
            GL11.glEnd();
            boundPage = -2;
        }
    }

    public float renderChar(char ch, boolean italic) {
        if (ch == 32) {
            return 4.0F;
        } else {
            int characterIndex = characterDictionary.indexOf(ch);
            return characterIndex != -1 && !fontRenderer.unicodeFlag ? renderDefaultChar(characterIndex, italic) : renderUnicodeChar(ch, italic);
        }
    }

    private float renderUnicodeChar(char characterIndex, boolean italic) {
        if (fontRenderer.glyphWidth[characterIndex] == 0) {
            return 0.0F;
        } else {
            // im not deobfuscating this lmao
            int i = characterIndex / 256;
            int j = this.fontRenderer.glyphWidth[characterIndex] >>> 4;
            int k = this.fontRenderer.glyphWidth[characterIndex] & 15;
            float f = (float) j;
            float f1 = (float) (k + 1);
            float f2 = (float) (characterIndex % 16 * 16) + f;
            float f3 = (float) ((characterIndex & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = italic ? 1.0F : 0.0F;
            if (startDrawing(i)) {
                GL11.glVertex2f(this.fontRenderer.posX + f5, this.fontRenderer.posY);
            }
            GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
            GL11.glVertex2f(this.fontRenderer.posX + f5, this.fontRenderer.posY);
            GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex2f(this.fontRenderer.posX + f5, this.fontRenderer.posY + 7.99F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
            GL11.glVertex2f(this.fontRenderer.posX + f4 / 2.0F + f5, this.fontRenderer.posY);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex2f(this.fontRenderer.posX + f4 / 2.0F - f5, this.fontRenderer.posY + 7.99F);
            GL11.glVertex2f(this.fontRenderer.posX + f4 / 2.0F - f5, this.fontRenderer.posY + 7.99F);
            return (f1 - f) / 2.0F + 1.0F;
        }
    }

    private float renderDefaultChar(int characterIndex, boolean italic) {
        int characterX = characterIndex % 16 * 8;
        int characterY = characterIndex / 16 * 8;
        int xModifier = italic ? 1 : 0;
        float charWidth = getCharWidthFloat(characterDictionary.charAt(characterIndex));
        float f = charWidth - 0.01F;

        if (startDrawing(-1)) {
            GL11.glVertex2f(fontRenderer.posX + xModifier, fontRenderer.posY);
        }

        GL11.glTexCoord2f((float) characterX / 128.0F, (float) characterY / 128.0F);
        GL11.glVertex2f(fontRenderer.posX + (float) xModifier, fontRenderer.posY);
        GL11.glTexCoord2f((float) characterX / 128.0F, ((float) characterY + 7.99F) / 128.0F);
        GL11.glVertex2f(fontRenderer.posX - (float) xModifier, fontRenderer.posY + 7.99F);
        GL11.glTexCoord2f(((float) characterX + f - 1.0F) / 128.0F, (float) characterY / 128.0F);
        GL11.glVertex2f(fontRenderer.posX + f - 1.0F + (float) xModifier, fontRenderer.posY);
        GL11.glTexCoord2f(((float) characterX + f - 1.0F) / 128.0F, ((float) characterY + 7.99F) / 128.0F);
        GL11.glVertex2f(fontRenderer.posX + f - 1.0F - (float) xModifier, fontRenderer.posY + 7.99F);
        GL11.glVertex2f(fontRenderer.posX + f - 1.0F - (float) xModifier, fontRenderer.posY + 7.99F);
        endDrawing();
        return charWidth;
    }

    private boolean startDrawing(int page) {
        if (drawing && page != boundPage) {
            endDrawing();
        }

        if (!drawing) {
            bindTexture(page == -1 ? fontRenderer.locationFontTexture : getUnicodePageLocation(page));
            boundPage = page;
            drawing = true;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            return false;
        }

        return true;
    }

    public int getStringWidth(String text) {
        Map<String, Integer> stringWidthCache = enhancedFontRenderer.getStringWidthCache();

        if (!PatcherConfig.optimizedFontRenderer) {
            if (stringWidthCache.size() != 0) {
                stringWidthCache.clear();
            }

            return getUncachedWidth(text);
        }

        if (text.isEmpty()) {
            return 0;
        }

        if (stringWidthCache.size() > 5000) {
            stringWidthCache.clear();
        }

        return stringWidthCache.computeIfAbsent(text, width -> getUncachedWidth(text));
    }

    private int getUncachedWidth(String text) {
        if (text.isEmpty()) {
            return 0;
        } else {
            // also definitely not deobfuscating this one
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
                    ++i;
                }
            }

            return (int) i;
        }
    }

    private ResourceLocation getUnicodePageLocation(int page) {
        if (unicodePageLocations[page] == null) {
            unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
        }

        return unicodePageLocations[page];
    }

    private float getCharWidthFloat(char c) {
        return fontRenderer.getCharWidth(c);
    }

    private static class RenderPair {
        private final float posX;
        private float width;

        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

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
