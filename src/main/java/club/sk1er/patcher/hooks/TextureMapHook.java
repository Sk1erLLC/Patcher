package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.profiler.Profiler;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.SharedDrawable;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TextureMapHook {

    private static final Object lock = new Object();
    private static final Profiler mcProfiler = Minecraft.getMinecraft().mcProfiler;
    private static final AtomicInteger count = new AtomicInteger(0);
    private static final ExecutorService service = Executors.newFixedThreadPool(1);
    private static SharedDrawable drawable;
    private static CountDownLatch latch;

    public static void latch() {
        synchronized (lock) {
            if (latch == null && count.get() == 0) {
                return;
            }

            latch = new CountDownLatch(count.get());
        }

        try {
            mcProfiler.startSection("texture_wait");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch = null;
        count.set(0);
        mcProfiler.endSection();
    }

    public static boolean updateAnimation(TextureMap instance, List<TextureAtlasSprite> spriteList) {
        if (PatcherConfig.asyncMipmapUpdates) {
            count.incrementAndGet();

            Runnable runnable = () -> {
                synchronized (lock) {
                    if (drawable != null) {
                        try {
                            drawable.makeCurrent();
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D, instance.getGlTextureId());

                            for (TextureAtlasSprite textureAtlasSprite : spriteList) {
                                textureAtlasSprite.updateAnimation();
                            }

                            drawable.releaseContext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (latch != null) {
                        latch.countDown();
                    } else {
                        count.decrementAndGet();
                    }
                }
            };

            if (drawable == null) {
                try {
                    drawable = new SharedDrawable(Display.getDrawable());
                } catch (LWJGLException e) {
                    e.printStackTrace();
                }
            }

            service.execute(runnable);
        }

        return PatcherConfig.asyncMipmapUpdates;
    }
}
