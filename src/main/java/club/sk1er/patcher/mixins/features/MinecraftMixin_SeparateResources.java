package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.IReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin_SeparateResources {

    //#if MC==10809
    @Shadow private SoundHandler mcSoundHandler;
    @Shadow private IReloadableResourceManager mcResourceManager;
    @Shadow public abstract void refreshResources();

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V", ordinal = 0))
    private void patcher$separateSoundReloading(Minecraft minecraft) {
        if (PatcherConfig.separateResourceLoading) {
            mcSoundHandler.onResourceManagerReload(mcResourceManager);
        } else {
            refreshResources();
        }
    }
    //#endif
}
