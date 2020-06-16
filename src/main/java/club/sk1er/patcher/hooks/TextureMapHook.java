package club.sk1er.patcher.hooks;

import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumChatFormatting;
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
    private static final ExecutorService service = Executors.newFixedThreadPool(1, r -> new Thread(r, "Mipmap Updater"));
    private static SharedDrawable drawable;
    private static CountDownLatch latch;
    private static TextureMap instance;
    private static List<TextureAtlasSprite> spriteList;
    private static boolean setup;

    public static void latch() {
        if(latch == null) return;
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
            synchronized (lock) {
                count.incrementAndGet();
                latch = new CountDownLatch(count.get());
            }
            TextureMapHook.instance = instance;
            TextureMapHook.spriteList = spriteList;
            if (drawable == null) {
                try {
                    drawable = new SharedDrawable(Display.getDrawable());
                } catch (LWJGLException e) {
                    e.printStackTrace();
                }
            }
            if (drawable == null) {
                MinecraftUtils.sendMessage(EnumChatFormatting.RED + "[Patcher] An error occurred while initializing async Mipmap updates and therefore it has been disabled.");
                PatcherConfig.asyncMipmapUpdates = false;
                return false; //Failed to create new drawable, run normally and disable
            }
            service.execute(TextureMapHook::update);
        } else latch = null;

        return PatcherConfig.asyncMipmapUpdates;
    }

    private static void setup() {
        if (!setup) {
            setup = true;
            try {
                drawable.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void update() {

        try {
            setup();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, instance.getGlTextureId());
            for (TextureAtlasSprite textureAtlasSprite : spriteList) {
                textureAtlasSprite.updateAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            latch.countDown();
            count.decrementAndGet();
        }
    }
}
