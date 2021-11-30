package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.MinecraftHook;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_BetterKeybindHandling {
    //#if MC==10809
    @Shadow @Final public static boolean isRunningOnMac;

    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    private void patcher$makeKeysReRegister(CallbackInfo ci) {
        if (PatcherConfig.newKeybindHandling && !Minecraft.isRunningOnMac) {
            MinecraftHook.updateKeyBindState();
        }
    }
    //#endif
}
