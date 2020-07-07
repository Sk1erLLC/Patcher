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

import club.sk1er.patcher.asm.FallbackResourceManagerTransformer;
import club.sk1er.patcher.database.AssetsDatabase;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Used in {@link FallbackResourceManagerTransformer#transform(ClassNode, String)}
 */
public class FallbackResourceManagerHook {
    public static final Set<String> negativeResourceCache = new HashSet<>();
    public static final AssetsDatabase database = new AssetsDatabase();

    static {
        try {
            negativeResourceCache.addAll(database.getAllNegative());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void clearCache() {
        negativeResourceCache.clear();
    }

    public static IResource getCachedResource(final FallbackResourceManager manager, final ResourceLocation location) throws IOException {
        if (negativeResourceCache.contains(location.toString())) {
            throw new FileNotFoundException(location.toString());
        }

        ResourceLocation mcMetaLocation = FallbackResourceManager.getLocationMcmeta(location);

        InputStream mcMetaStream = null;
        for (int i = manager.resourcePacks.size() - 1; i >= 0; --i) {
            IResourcePack currentPack = manager.resourcePacks.get(i);

            if (mcMetaStream == null) {
                InputStream safe = getFromFile(currentPack, mcMetaLocation);
                if (safe != null) {
                    mcMetaStream = safe;
                }
            }

            InputStream stream = getFromFile(currentPack, location);
            if (stream != null) {
                return new SimpleResource(currentPack.getPackName(), location, stream, mcMetaStream, manager.frmMetadataSerializer);
            }
        }

        negativeResourceCache.add(location.getResourcePath());
        throw new FileNotFoundException(location.toString());
    }

    public static InputStream getFromFile(IResourcePack pack, ResourceLocation location) {
        try {
            return new BufferedInputStream(pack.getInputStream(location));
        } catch (Throwable ignored) {
        }

        return null;
    }
}
