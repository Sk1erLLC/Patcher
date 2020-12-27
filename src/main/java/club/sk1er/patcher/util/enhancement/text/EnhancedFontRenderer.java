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

import net.modcore.api.utils.Multithreading;
import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.hash.StringHash;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.renderer.GLAllocation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EnhancedFontRenderer implements Enhancement {

    private static final List<EnhancedFontRenderer> instances = new ArrayList<>();
    private final List<StringHash> obfuscated = new ArrayList<>();
    private final Map<String, Integer> stringWidthCache = new HashMap<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private final Cache<StringHash, CachedString> stringCache = Caffeine.newBuilder()
        .writer(new RemovalListener())
        .executor(Multithreading.getPool())
        .maximumSize(5000).build();

    public EnhancedFontRenderer() {
        instances.add(this);
    }

    public static List<EnhancedFontRenderer> getInstances() {
        return instances;
    }

    @Override
    public String getName() {
        return "Enhanced Font Renderer";
    }

    @Override
    public void tick() {
        stringCache.invalidateAll(obfuscated);
        obfuscated.clear();
    }

    public int getGlList() {
        final Integer poll = glRemoval.poll();
        return poll == null ? GLAllocation.generateDisplayLists(1) : poll;
    }

    public Queue<Integer> getGlRemoval() {
        return glRemoval;
    }

    public void invalidate() {
        stringCache.invalidateAll();
    }

    public CachedString get(StringHash key) {
        return stringCache.getIfPresent(key);
    }

    public void cache(StringHash key, CachedString value) {
        stringCache.put(key, value);
    }

    public Map<String, Integer> getStringWidthCache() {
        return stringWidthCache;
    }

    public void invalidateAll() {
        this.stringCache.invalidateAll();
    }

    public List<StringHash> getObfuscated() {
        return obfuscated;
    }

    private class RemovalListener implements CacheWriter<StringHash, CachedString> {

        @Override
        public void write(@Nonnull StringHash key, @Nonnull CachedString value) {

        }

        @Override
        public void delete(@Nonnull StringHash key, CachedString value, @Nonnull RemovalCause cause) {
            if (value == null) {
                return;
            }

            glRemoval.add(value.getListId());
        }
    }
}
