package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_OptimizedWorldSwapping {
    @Redirect(
        method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V")
    )
    private void patcher$optimizedWorldSwapping() {
        if (!PatcherConfig.optimizedWorldSwapping) {
            System.gc();
        }
    }
}
