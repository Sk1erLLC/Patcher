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

package club.sk1er.patcher.util.enhancement.item;

import net.modcore.api.utils.Multithreading;
import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.hash.impl.ItemHash;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.minecraft.client.renderer.GLAllocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EnhancedItemRenderer implements Enhancement {

    private static final List<EnhancedItemRenderer> instances = new ArrayList<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private final Cache<ItemHash, Integer> itemCache = Caffeine.newBuilder()
        .maximumSize(5000)
        .writer(new RemovalListener())
        .executor(Multithreading.getPool())
        .build();

    @Override
    public String getName() {
        return "Enhanced Item Renderer";
    }

    public void invalidateAll() {
        itemCache.invalidateAll();
    }

    public int getGlList() {
        Integer poll = glRemoval.poll();
        return poll == null ? GLAllocation.generateDisplayLists(1) : poll;
    }

    public static List<EnhancedItemRenderer> getInstances() {
        return instances;
    }

    public Cache<ItemHash, Integer> getItemCache() {
        return itemCache;
    }

    private class RemovalListener implements CacheWriter<ItemHash, Integer> {
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
        public void write(@NonNull ItemHash key, @NonNull Integer value) {

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
        public void delete(@NonNull ItemHash key, @Nullable Integer value, @NonNull RemovalCause cause) {
            if (value == null) {
                return;
            }

            glRemoval.add(value);
        }
    }
}
