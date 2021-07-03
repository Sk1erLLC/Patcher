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

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.asm.FallbackResourceManagerTransformer;
import club.sk1er.patcher.database.AssetsDatabase;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Used in {@link FallbackResourceManagerTransformer#transform(ClassNode, String)}
 */
public class FallbackResourceManagerHook {
    public static final Set<String> negativeResourceCache = new HashSet<>();
    public static final AssetsDatabase database = new AssetsDatabase();
    public static final Map<String, String> resourceMap = new HashMap<>();

    static {
        try {
            negativeResourceCache.addAll(database.getAllNegative());
            resourceMap.putAll(database.getAllMap());
        } catch (IOException e) {
            Patcher.instance.getLogger().error("Failed to fill negative resource cache/resource map.", e);
        }
    }

    @SuppressWarnings("unused")
    public static void clearCache() {
        negativeResourceCache.clear();
        resourceMap.clear();
    }

    public static IResource getCachedResource(final FallbackResourceManager manager, final ResourceLocation location) throws IOException {
        if (negativeResourceCache.contains(location.toString())) {
            throw new FileNotFoundException(location.toString());
        }

        ResourceLocation mcMetaLocation = FallbackResourceManager.getLocationMcmeta(location);

        InputStream mcMetaStream = null;
        final String resourceLocation = resourceMap.get(location.toString());
        if (resourceLocation != null) {
            for (IResourcePack resourcePack : manager.resourcePacks) {
                if (resourcePack.getPackName().equalsIgnoreCase(resourceLocation)) {
                    final InputStream fromFile = getFromFile(resourcePack, location);
                    if (fromFile != null)
                        return new SimpleResource(resourcePack.getPackName(), location, fromFile,
                            getFromFile(resourcePack, mcMetaLocation), manager.frmMetadataSerializer);
                }
            }
        }

        for (int i = manager.resourcePacks.size() - 1; i >= 0; --i) {
            IResourcePack currentPack = manager.resourcePacks.get(i);
            if (currentPack instanceof FileResourcePack && !currentPack.resourceExists(location)) {
                continue;
            }

            if (mcMetaStream == null) {
                InputStream safe = getFromFile(currentPack, mcMetaLocation);
                if (safe != null) {
                    mcMetaStream = safe;
                }
            }

            InputStream stream = getFromFile(currentPack, location);
            if (stream != null) {
                mapResource(location, currentPack.getPackName());
                return new SimpleResource(currentPack.getPackName(), location, stream, mcMetaStream, manager.frmMetadataSerializer);
            }
        }

        negativeResourceCache.add(location.toString());
        throw new FileNotFoundException(location.toString());
    }

    public static void mapResource(ResourceLocation location, String name) {
        resourceMap.put(location.toString(), name);
    }

    public static InputStream getFromFile(IResourcePack pack, ResourceLocation location) {
        try {
            return new BufferedInputStream(pack.getInputStream(location));
        } catch (Throwable ignored) {
        }

        return null;
    }
}
