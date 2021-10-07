package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.ResourcePackRepositoryHook;
import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourcePackRepository.class)
public class ResourcePackRepositoryMixin_FasterSearching {

    @Inject(method = "updateRepositoryEntriesAll", at = @At("HEAD"), cancellable = true)
    private void patcher$searchUsingSet(CallbackInfo ci) {
        if (PatcherConfig.labyModMoment) {
            // todo: move this hook into this class
            //  the funky "repository.new Entry(file)" line has me stumped
            ResourcePackRepositoryHook.updateRepositoryEntriesAll((ResourcePackRepository) (Object) this);
            ci.cancel();
        }
    }
}
