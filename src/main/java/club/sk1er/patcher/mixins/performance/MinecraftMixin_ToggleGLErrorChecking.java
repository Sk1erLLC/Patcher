package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_ToggleGLErrorChecking {

    @Shadow private boolean enableGLErrorChecking;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void patcher$disableGlErrorChecking(CallbackInfo ci) {
        this.enableGLErrorChecking = false;
    }
}
