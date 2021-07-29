package club.sk1er.patcher.mixins.hudcaching;

import club.sk1er.patcher.screen.render.caching.HUDCaching;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public class FramebufferMixin_HUDCaching {

    @Inject(method = "bindFramebuffer", at = @At("HEAD"), cancellable = true)
    public void patcher$bindHUDCachingBuffer(boolean viewport, CallbackInfo ci) {
        final Framebuffer framebuffer = (Framebuffer) (Object) this;
        if (HUDCaching.renderingCacheOverride && framebuffer == Minecraft.getMinecraft().getFramebuffer()) {
            HUDCaching.framebuffer.bindFramebuffer(viewport);
            ci.cancel();
        }
    }
}
