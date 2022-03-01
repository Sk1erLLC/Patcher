package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;
import java.util.List;

@Mixin(ResourcePackRepository.class)
public interface ResourcePackRepositoryAccessor {
    @Invoker
    List<File> invokeGetResourcePackFiles();

    @Accessor
    void setRepositoryEntriesAll(List<ResourcePackRepository.Entry> entries);
}
