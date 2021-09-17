package club.sk1er.patcher.util.enhancement;

import club.sk1er.patcher.hooks.FontRendererHook;
import club.sk1er.patcher.util.enhancement.text.EnhancedFontRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances()) {
            enhancedFontRenderer.invalidateAll();
        }

        FontRendererHook.forceRefresh = true;
    }
}
