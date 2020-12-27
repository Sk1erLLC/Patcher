package club.sk1er.patcher.hooks;

import net.modcore.api.utils.Multithreading;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.IOException;

@SuppressWarnings("unused")
public class ThreadDownloadImageDataHook extends SimpleTexture {
    public ThreadDownloadImageDataHook(ResourceLocation textureResourceLocation) {
        super(textureResourceLocation);
    }

    public static void getImprovedCacheLoading(ThreadDownloadImageData data) {
        if (data.imageThread == null) {
            Multithreading.runAsync(() -> {
                if (data.cacheFile != null && data.cacheFile.isFile()) {
                    ThreadDownloadImageData.logger.debug("Loading http texture from local cache ({})", data.cacheFile);

                    try {
                        data.bufferedImage = ImageIO.read(data.cacheFile);

                        if (data.imageBuffer != null) {
                            data.setBufferedImage(data.imageBuffer.parseUserSkin(data.bufferedImage));
                        }
                    } catch (IOException ioexception) {
                        ThreadDownloadImageData.logger.error("Couldn't load skin " + data.cacheFile, ioexception);
                        data.loadTextureFromServer();
                    }
                } else {
                    data.loadTextureFromServer();
                }
            });
        }
    }
}
