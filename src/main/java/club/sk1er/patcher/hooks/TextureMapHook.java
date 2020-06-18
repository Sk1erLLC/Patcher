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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class TextureMapHook {

    private static final Object lock = new Object();
    private static final Profiler mcProfiler = Minecraft.getMinecraft().mcProfiler;
    private static final AtomicInteger count = new AtomicInteger(0);
    private static final Worker target = new Worker();
    private static final Thread task = new Thread(target, "Mipmap Updater");
    private static SharedDrawable drawable;
    private static CountDownLatch latch;
    private static TextureMap instance;
    private static List<TextureAtlasSprite> spriteList;
    private static boolean setup = false;


    public static void latch() {
        if (latch == null) return;
        try {
            mcProfiler.startSection("texture_wait");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            latch = null;
            count.set(0);
        }
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
                    task.start();
                } catch (LWJGLException e) {
                    e.printStackTrace();
                }
            }
            if (drawable == null) {
                MinecraftUtils.sendMessage(EnumChatFormatting.RED + "[Patcher] An error occurred while initializing async Mipmap updates and therefore it has been disabled.");
                PatcherConfig.asyncMipmapUpdates = false;
                return false; //Failed to create new drawable, run normally and disable
            }
            target.execute(TextureMapHook::update);
        } else synchronized (lock) {
            latch = null;
        }

        return PatcherConfig.asyncMipmapUpdates;
    }

    private static void setup() {
        try {
            if (!setup) {
                setup = true;
                drawable.makeCurrent();
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private static void update() {
        try {
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

    public static class Worker implements Runnable {
        private final BlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            setup();

            try {
                while (true) {
                    runnables.take().run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void execute(Runnable update) {
            runnables.add(update);
        }
    }
}
