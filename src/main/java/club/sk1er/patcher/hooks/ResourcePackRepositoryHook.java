package club.sk1er.patcher.hooks;

import club.sk1er.patcher.mixins.accessors.ResourcePackRepositoryAccessor;
import net.minecraft.client.resources.ResourcePackRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ResourcePackRepositoryHook {
    public static void updateRepositoryEntriesAll(ResourcePackRepository r) {
        ResourcePackRepositoryAccessor repository = (ResourcePackRepositoryAccessor) r;

        final Map<Integer, ResourcePackRepository.Entry> all = new HashMap<>();
        for (ResourcePackRepository.Entry entry : r.getRepositoryEntriesAll()) {
            all.put(entry.hashCode(), entry);
        }

        final Set<ResourcePackRepository.Entry> newSet = new LinkedHashSet<>();
        for (File file : repository.callGetResourcePackFiles()) {
            final ResourcePackRepository.Entry entry = r.new Entry(file);
            final int entryHash = entry.hashCode();
            if (!all.containsKey(entryHash)) {
                try {
                    entry.updateResourcePack();
                    newSet.add(entry);
                } catch (Exception ignored) {
                    newSet.remove(entry);
                }
            } else {
                newSet.add(all.get(entryHash));
            }
        }

        for (ResourcePackRepository.Entry entry : all.values()) {
            if (!newSet.contains(entry)) {
                entry.closeResourcePack();
            }
        }

        repository.setRepositoryEntriesAll(new ArrayList<>(newSet));
    }
}
