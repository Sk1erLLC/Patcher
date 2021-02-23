package club.sk1er.patcher.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;

@SuppressWarnings("unused")
public class GuiScreenResourcePacksHook {
    public static void clearHandles() {
        final ResourcePackRepository repository = Minecraft.getMinecraft().getResourcePackRepository();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) {
            final IResourcePack current = repository.getResourcePackInstance();
            if (current == null || !entry.getResourcePackName().equals(current.getPackName())) {
                entry.closeResourcePack();
            }
        }
    }
}
