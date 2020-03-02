package club.sk1er.patcher.hooks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FallbackResourceManagerHook {

  public static IResource getCachedResource(FallbackResourceManager manager, ResourceLocation location)
      throws IOException {
    IResourcePack iresourcepack = null;
    ResourceLocation resourcelocation = FallbackResourceManager.getLocationMcmeta(location);
    ByteArrayOutputStream packInfoCache = null;

    for (int i = manager.resourcePacks.size() - 1; i >= 0; --i) {
      IResourcePack iresourcepack1 = manager.resourcePacks.get(i);

      if (iresourcepack == null) {
        InputStream safe = getSafe(iresourcepack1, resourcelocation);
        if (safe != null) {
          iresourcepack = iresourcepack1;
          packInfoCache = new ByteArrayOutputStream();
          IOUtils.copy(safe, packInfoCache);
          safe.close();
        }
      }

      InputStream stream = getSafe(iresourcepack1, location);
      if (stream != null) {
        InputStream inputstream = null;

        if (iresourcepack != null) {
          inputstream = new ByteArrayInputStream(packInfoCache.toByteArray());
        }

        return new SimpleResource(
            iresourcepack1.getPackName(),
            location,
            stream,
            inputstream,
            manager.frmMetadataSerializer);
      }
    }

    throw new FileNotFoundException(location.toString());
  }

  private static InputStream getSafe(IResourcePack pack, ResourceLocation location) {
    try {
      return pack.getInputStream(location);
    } catch (Exception ignored) {
    }

    return null;
  }
}
