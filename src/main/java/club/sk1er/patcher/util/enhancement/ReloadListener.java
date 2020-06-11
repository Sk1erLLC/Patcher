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

import club.sk1er.patcher.hooks.FontRendererHook;
import club.sk1er.patcher.util.enhancement.item.EnhancedItemRenderer;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances()) {
            enhancedFontRenderer.invalidateAll();
        }

        for (EnhancedItemRenderer enhancedItemRenderer : EnhancedItemRenderer.getInstances()) {
            enhancedItemRenderer.invalidateAll();
        }

        FontRendererHook.forceRefresh = true;
    }
}
