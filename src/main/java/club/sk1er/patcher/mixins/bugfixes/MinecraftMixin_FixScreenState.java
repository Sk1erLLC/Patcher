package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_FixScreenState {

    @Shadow public boolean fullscreen;

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void patcher$resolveScreenState(CallbackInfo ci) {
        if (!this.fullscreen && SystemUtils.IS_OS_WINDOWS) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }
}
