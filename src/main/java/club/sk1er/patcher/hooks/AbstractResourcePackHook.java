package club.sk1er.patcher.hooks;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class AbstractResourcePackHook {
    public static BufferedImage getPackImage(BufferedImage image) {
        if (image == null) return null;
        if (image.getWidth() <= 64 && image.getHeight() <= 64) return image;

        final BufferedImage downscaledIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = downscaledIcon.getGraphics();
        graphics.drawImage(image, 0, 0, 64, 64, null);
        graphics.dispose();
        return downscaledIcon;
    }
}
