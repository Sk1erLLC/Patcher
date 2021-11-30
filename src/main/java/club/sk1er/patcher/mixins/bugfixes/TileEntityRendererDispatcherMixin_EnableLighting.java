package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin_EnableLighting {
    //#if MC==10809
    @Inject(method = "renderTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/BlockPos;I)I"))
    private void patcher$enableLighting(CallbackInfo ci) {
        RenderHelper.enableStandardItemLighting();
    }
    //#endif
}
