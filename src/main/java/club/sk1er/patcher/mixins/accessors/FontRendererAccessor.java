package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontRenderer.class)
public interface FontRendererAccessor {
    @Accessor
    ResourceLocation getLocationFontTexture();

    @Accessor
    TextureManager getRenderEngine();

    @Accessor
    float getPosX();

    @Accessor
    float getPosY();

    @Accessor
    void setPosX(float posX);

    @Accessor
    void setPosY(float posY);

    @Accessor
    float getRed();

    // Someone messed up the mappings
    @Accessor("green")
    float getBlue();

    // Someone messed up the mappings
    @Accessor("blue")
    float getGreen();

    @Accessor
    float getAlpha();

    @Accessor
    void setStrikethroughStyle(boolean b);

    @Accessor
    void setUnderlineStyle(boolean b);

    @Accessor
    void setItalicStyle(boolean b);

    @Accessor
    void setRandomStyle(boolean b);

    @Accessor
    void setBoldStyle(boolean b);

    @Accessor
    boolean isRandomStyle();

    @Accessor
    boolean isItalicStyle();

    @Accessor
    boolean isBoldStyle();
    
    @Accessor
    boolean isStrikethroughStyle();

    @Accessor
    boolean isUnderlineStyle();

    @Accessor
    byte[] getGlyphWidth();

    @Accessor
    void setTextColor(int color);

    @Accessor
    int[] getColorCode();
}
