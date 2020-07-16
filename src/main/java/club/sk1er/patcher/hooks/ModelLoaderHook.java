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

import club.sk1er.patcher.registry.AsyncBlockAndItems;
import com.google.common.base.Throwables;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ModelLoaderHook {

    public static IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry(ModelLoader modelLoader,
                                                                                   IModel missingModel,
                                                                                   Map<ModelResourceLocation, IModel> stateModels,
                                                                                   Set<ResourceLocation> textures) {
        AsyncBlockAndItems asyncBlockAndItems = new AsyncBlockAndItems(modelLoader);
        asyncBlockAndItems.load();

        try {
            missingModel = modelLoader.getModel(new ResourceLocation(ModelBakery.MODEL_MISSING.getResourceDomain(), ModelBakery.MODEL_MISSING.getResourcePath()));
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        stateModels.put(ModelBakery.MODEL_MISSING, missingModel);
        textures.remove(TextureMap.LOCATION_MISSING_TEXTURE);
        textures.addAll(ModelBakery.LOCATIONS_BUILTIN_TEXTURES);
        modelLoader.textureMap.loadSprites(modelLoader.resourceManager, map -> {
            for (ResourceLocation resource : textures) {
                map.registerSprite(resource);
            }
        });

        IFlexibleBakedModel missingBaked = missingModel.bake(missingModel.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

        for (Map.Entry<ModelResourceLocation, IModel> entry : stateModels.entrySet()) {
            if (entry.getValue() == modelLoader.getMissingModel()) {
                modelLoader.bakedRegistry.putObject(entry.getKey(), missingBaked);
            } else {
                modelLoader.bakedRegistry.putObject(entry.getKey(), entry.getValue().bake(entry.getValue().getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter()));
            }
        }

        return modelLoader.bakedRegistry;
    }
}
