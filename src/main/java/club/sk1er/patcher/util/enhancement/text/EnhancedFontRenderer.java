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

package club.sk1er.patcher.util.enhancement.text;

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.util.cache.CachedString;
import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.hash.StringHash;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.renderer.GLAllocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.SharedDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EnhancedFontRenderer implements Enhancement {

    private static SharedDrawable drawable;
    private static final List<EnhancedFontRenderer> instances = new ArrayList<>();
    private final List<StringHash> obfuscated = new ArrayList<>();
    private final Map<String, Integer> stringWidthCache = new HashMap<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private final Cache<StringHash, CachedString> stringCache = Caffeine.newBuilder()
        .writer(new RemovalListener())
        .executor(Multithreading.POOL)
        .maximumSize(5000).build();

    public EnhancedFontRenderer() {
        instances.add(this);
    }

    @Override
    public String getName() {
        return "Enhanced Font Renderer";
    }

    @Override
    public void tick() {
        stringCache.invalidateAll(obfuscated);
        obfuscated.clear();
        if (drawable == null) {
            try {
                drawable = new SharedDrawable(Display.getDrawable());
            } catch (LWJGLException e) {
                Patcher.instance.getLogger().error("Failed to create shared drawable.", e);
            }
        }
    }

    public void cache(StringHash key, CachedString value) {
        stringCache.put(key, value);
    }

    public CachedString get(StringHash key) {
        return stringCache.getIfPresent(key);
    }

    public int getGlList() {
        Integer poll = glRemoval.poll();

        if (poll == null) {
            return GLAllocation.generateDisplayLists(1);
        }

        return poll;
    }

    public void invalidateAll() {
        stringCache.invalidateAll();
    }

    public Map<String, Integer> getStringWidthCache() {
        return stringWidthCache;
    }

    public List<StringHash> getObfuscated() {
        return obfuscated;
    }

    public static List<EnhancedFontRenderer> getInstances() {
        return instances;
    }

    private class RemovalListener implements CacheWriter<StringHash, CachedString> {
        /***
         * Writes the value corresponding to the {@code key} to the external resource. The cache will
         * communicate a write when an entry in the cache is created or modified, except when that was
         * due to a load or computation.
         *
         * @param key the non-null key whose value should be written
         * @param value the value associated with {@code key} that should be written
         * @throws RuntimeException or Error, in which case the mapping is unchanged
         */
        @Override
        public void write(@NonNull StringHash key, @NonNull CachedString value) {
            // no-op
        }

        /**
         * Deletes the value corresponding to the {@code key} from the external resource. The cache will
         * communicate a delete when the entry is explicitly removed or evicted.
         *
         * @param key   the non-null key whose value was removed
         * @param value the value associated with {@code key}, or {@code null} if collected
         * @param cause the reason for which the entry was removed
         * @throws RuntimeException or Error, in which case the mapping is unchanged
         */
        @Override
        public void delete(@NonNull StringHash key, @Nullable CachedString value, @NonNull RemovalCause cause) {
            if (value == null) {
                return;
            }

            glRemoval.add(value.getListId());
        }
    }
}
