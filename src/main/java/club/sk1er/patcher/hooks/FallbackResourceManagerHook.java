package club.sk1er.patcher.hooks;

import club.sk1er.patcher.database.AssetsDatabase;
import club.sk1er.patcher.database.DatabaseReturn;
import club.sk1er.patcher.tweaker.asm.FallbackResourceManagerTransformer;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Used in {@link FallbackResourceManagerTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class FallbackResourceManagerHook {
    private static final AssetsDatabase database = new AssetsDatabase();
    private static final boolean DB = true;

    public static IResource getCachedResource(FallbackResourceManager manager, ResourceLocation location) throws IOException {
        ResourceLocation mcMetaLocation = FallbackResourceManager.getLocationMcmeta(location);
        if (DB) {
            DatabaseReturn data = database.getData(location.getResourcePath());
            if (data != null) {
                return new SimpleResource(data.getPackName(),
                    location,
                    new ByteArrayInputStream(data.getData()),
                    data.getMcMeta() != null ? new ByteArrayInputStream(data.getMcMeta()) : null,
                    manager.frmMetadataSerializer);
            }
        }
        byte[] rawMcMeta = null;
        for (int i = manager.resourcePacks.size() - 1; i >= 0; --i) {
            IResourcePack currentPack = manager.resourcePacks.get(i);

            if (rawMcMeta == null) {
                InputStream safe = getFromFile(currentPack, mcMetaLocation);
                if (safe != null) {
                    rawMcMeta = readCopy(safe);
                }
            }

            InputStream stream = getFromFile(currentPack, location);
            if (stream != null) {
                InputStream mcMetaData = null;
                if (rawMcMeta != null) {
                    mcMetaData = new ByteArrayInputStream(rawMcMeta);
                }
                byte[] mainData = readCopy(stream);
                if (DB)
                    database.update(currentPack.getPackName(), location.getResourcePath(), mainData, rawMcMeta);
                return new SimpleResource(
                    currentPack.getPackName(),
                    location,
                    new ByteArrayInputStream(mainData),
                    mcMetaData,
                    manager.frmMetadataSerializer);
            }
        }
        throw new FileNotFoundException(location.toString());
    }

    private static byte[] readCopy(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, out);
        return out.toByteArray();
    }

    private static InputStream getFromFile(IResourcePack pack, ResourceLocation location) {

        try {
            BufferedInputStream inputStream = new BufferedInputStream(pack.getInputStream(location));
            byte[] bytes = readCopy(inputStream);
            return new ByteArrayInputStream(bytes);
        } catch (Throwable ignored) {
        }

        return null;
    }

    static class Data {
        String name;
        ResourceLocation location;
        byte[] stream;
        byte[] mcMeta;

        public Data(String name, ResourceLocation location, byte[] stream, byte[] mcMeta) {
            this.name = name;
            this.location = location;
            this.stream = stream;
            this.mcMeta = mcMeta;
        }

        @Override
        public String toString() {
            return "Data{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", stream=" + Arrays.toString(stream) +
                ", mcMeta=" + Arrays.toString(mcMeta) +
                '}';
        }
    }
}
