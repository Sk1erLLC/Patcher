package club.sk1er.patcher.hooks;

import net.minecraft.client.resources.ResourcePackRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ResourcePackRepositoryHook {
    public static void updateRepositoryEntriesAll(ResourcePackRepository repository) {
        final Map<Integer, ResourcePackRepository.Entry> all = new HashMap<>();
        for (ResourcePackRepository.Entry entry : repository.repositoryEntriesAll) {
            all.put(entry.hashCode(), entry);
        }

        final Set<ResourcePackRepository.Entry> newSet = new LinkedHashSet<>();
        for (File file : repository.getResourcePackFiles()) {
            final ResourcePackRepository.Entry entry = repository.new Entry(file);
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

        repository.repositoryEntriesAll = new ArrayList<>(newSet);
    }
}
