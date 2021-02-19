package club.sk1er.patcher.hooks;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TileEntityBannerRendererHook {

    public static ResourceLocation getPatternResourceLocation(TileEntityBanner banner) {
        final String texture = banner.getPatternResourceLocation();

        if (texture.isEmpty()) {
            return null;
        } else {
            final Map<String, TileEntityBannerRenderer.TimedBannerTexture> designs = TileEntityBannerRenderer.DESIGNS;
            TileEntityBannerRenderer.TimedBannerTexture timedTexture = designs.get(texture);
            if (timedTexture == null) {
                if (designs.size() >= 256 && !freeCacheSlot()) {
                    return TileEntityBannerRenderer.BANNERTEXTURES;
                }

                final List<TileEntityBanner.EnumBannerPattern> patternList = banner.getPatternList();
                final List<EnumDyeColor> colorList = banner.getColorList();
                final List<String> patternPath = Lists.newArrayList();

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

    private static boolean freeCacheSlot() {
        final long start = System.currentTimeMillis();
        final Map<String, TileEntityBannerRenderer.TimedBannerTexture> designs = TileEntityBannerRenderer.DESIGNS;
        final Iterator<String> iterator = designs.keySet().iterator();

        while (iterator.hasNext()) {
            final String next = iterator.next();
            final TileEntityBannerRenderer.TimedBannerTexture timedTexture = designs.get(next);

            if ((start - timedTexture.systemTime) > 5000L) {
                Minecraft.getMinecraft().getTextureManager().deleteTexture(timedTexture.bannerTexture);
                iterator.remove();
                return true;
            }
        }

        return designs.size() < 256;
    }
}
