package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlStateManager.class)
public interface GlStateManagerAccessor {
    @Accessor
    static GlStateManager.Color getColorState() {
        throw new UnsupportedOperationException("Mixin failed to inject!");
    }

    @Accessor
    static GlStateManager.TextureState[] getTextureState() {
        throw new UnsupportedOperationException("Mixin failed to inject!");
    }

    @Accessor
    static int getActiveTextureUnit() {
        throw new UnsupportedOperationException("Mixin failed to inject!");
    }
}
