package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.MinecraftHook;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_WindowedFullscreen {
    @Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
    private void patcher$windowedFullscreen(CallbackInfo ci) {
        if (MinecraftHook.fullscreen()) ci.cancel();
    }
}
