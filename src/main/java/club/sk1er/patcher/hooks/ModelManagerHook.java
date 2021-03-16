package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraftforge.client.model.ModelLoader;

@SuppressWarnings("unused")
public class ModelManagerHook {
    public static ModelBakery createModelBakery(IResourceManager resourceManager, TextureMap textureMap, BlockModelShapes shapes) {
        return PatcherConfig.replaceModelLoader
            ? new ModelBakery(resourceManager, textureMap, shapes)
            : new ModelLoader(resourceManager, textureMap, shapes);
    }
}
