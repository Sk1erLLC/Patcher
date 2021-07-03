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

package club.sk1er.patcher.util.enhancement;

import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;

import java.util.HashMap;
import java.util.Map;

public class EnhancementManager {

    private static final EnhancementManager instance = new EnhancementManager();
    private final Map<Class<? extends Enhancement>, Enhancement> enhancementMap = new HashMap<>();

    public EnhancementManager() {
        enhancementMap.put(EnhancedFontRenderer.class, new EnhancedFontRenderer());
    }

    public void tick() {
        for (Map.Entry<Class<? extends Enhancement>, Enhancement> entry : enhancementMap.entrySet()) {
            entry.getValue().tick();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Enhancement> T getEnhancement(Class<T> enhancement) {
        return (T) enhancementMap.get(enhancement);
    }

    public static EnhancementManager getInstance() {
        return instance;
    }
}
