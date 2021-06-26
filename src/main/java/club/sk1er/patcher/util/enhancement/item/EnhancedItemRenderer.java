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

import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.enhancement.hash.impl.ItemHash;
import gg.essential.lib.caffeine.cache.Cache;
import gg.essential.lib.caffeine.cache.Caffeine;
import net.minecraft.client.renderer.GLAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EnhancedItemRenderer implements Enhancement {

    private static final List<EnhancedItemRenderer> instances = new ArrayList<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private final Cache<ItemHash, Integer> itemCache = Caffeine.newBuilder()
        .removalListener((key, value, cause) -> {
            if (value == null) return;
            glRemoval.add((Integer) value);
        }).executor(POOL).maximumSize(5000).build();

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
}
