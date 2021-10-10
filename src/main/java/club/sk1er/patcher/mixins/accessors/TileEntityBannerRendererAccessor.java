package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TileEntityBannerRenderer.class)
public interface TileEntityBannerRendererAccessor {
    @Accessor("DESIGNS")
    static Map<String, TileEntityBannerRenderer.TimedBannerTexture> getDesigns() {
        throw new UnsupportedOperationException("Mixin has not been injected!");
    }

    @Accessor("BANNERTEXTURES")
    static ResourceLocation getBannerTextures() {
        throw new UnsupportedOperationException("Mixin has not been injected!");
    }
}
