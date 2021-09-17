package club.sk1er.patcher.mixins;

import net.minecraft.block.Block;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.BlockModelRenderer.AmbientOcclusionFace")
public class BlockModelRendererMixin_SmoothLighting {
    @Redirect(method = "updateVertexBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isTranslucent()Z", opcode = Opcodes.INVOKEVIRTUAL))
    private boolean patcher$betterSmoothLighting(Block block) {
        return !block.isVisuallyOpaque() || block.getLightOpacity() == 0;
    }
}
