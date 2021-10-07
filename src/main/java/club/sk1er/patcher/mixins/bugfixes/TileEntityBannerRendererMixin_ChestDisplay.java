package club.sk1er.patcher.mixins.bugfixes;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(TileEntityBannerRenderer.class)
public class TileEntityBannerRendererMixin_ChestDisplay {

    /**
     * @author asbyth
     * @reason Resolve banners in chests not displaying once cache is full
     */
    @Overwrite
    private ResourceLocation func_178463_a(TileEntityBanner banner) {
        String texture = banner.getPatternResourceLocation();

        if (texture.isEmpty()) {
            return null;
        } else {
            Map<String, TileEntityBannerRenderer.TimedBannerTexture> designs = TileEntityBannerRenderer.DESIGNS;
            TileEntityBannerRenderer.TimedBannerTexture timedTexture = designs.get(texture);
            if (timedTexture == null) {
                if (designs.size() >= 256 && !this.patcher$freeCacheSlot()) {
                    return TileEntityBannerRenderer.BANNERTEXTURES;
                }

                List<TileEntityBanner.EnumBannerPattern> patternList = banner.getPatternList();
                List<EnumDyeColor> colorList = banner.getColorList();
                List<String> patternPath = Lists.newArrayList();

                for (TileEntityBanner.EnumBannerPattern pattern : patternList) {
                    patternPath.add("textures/entity/banner/" + pattern.getPatternName() + ".png");
                }

                timedTexture = new TileEntityBannerRenderer.TimedBannerTexture();
                timedTexture.bannerTexture = new ResourceLocation(texture);
                Minecraft.getMinecraft().getTextureManager().loadTexture(timedTexture.bannerTexture, new LayeredColorMaskTexture(TileEntityBannerRenderer.BANNERTEXTURES, patternPath, colorList));
                TileEntityBannerRenderer.DESIGNS.put(texture, timedTexture);
            }

            timedTexture.systemTime = System.currentTimeMillis();
            return timedTexture.bannerTexture;
        }
    }

    @Unique
    private boolean patcher$freeCacheSlot() {
        long start = System.currentTimeMillis();
        Map<String, TileEntityBannerRenderer.TimedBannerTexture> designs = TileEntityBannerRenderer.DESIGNS;
        Iterator<String> iterator = designs.keySet().iterator();

        while (iterator.hasNext()) {
            String next = iterator.next();
            TileEntityBannerRenderer.TimedBannerTexture timedTexture = designs.get(next);

            if ((start - timedTexture.systemTime) > 5000L) {
                Minecraft.getMinecraft().getTextureManager().deleteTexture(timedTexture.bannerTexture);
                iterator.remove();
                return true;
            }
        }

        return designs.size() < 256;
    }
}
