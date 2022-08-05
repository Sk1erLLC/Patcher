package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin_ResolveLockups {
    @Shadow private int glBufferId;

    // inject at INVOKE rather than HEAD as OptiFine changes this method for its Render Regions option
    @Inject(method = "bufferData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;bindBuffer()V"), cancellable = true)
    private void patcher$ignoreRemovedBuffers(ByteBuffer byteBuffer, CallbackInfo ci) {
        if (this.glBufferId == -1) {
            ci.cancel();
        }
    }
}
