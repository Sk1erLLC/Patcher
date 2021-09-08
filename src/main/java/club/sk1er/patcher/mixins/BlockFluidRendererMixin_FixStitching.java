package club.sk1er.patcher.mixins;

import net.minecraft.client.renderer.BlockFluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockFluidRenderer.class)
public class BlockFluidRendererMixin_FixStitching {
    @ModifyConstant(method = "renderFluid", constant = @Constant(floatValue = 0.001F))
    private float patcher$fixFluidStitching(float original) {
        return 0.0F;
    }
}
