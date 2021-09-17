package club.sk1er.patcher.mixins;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityBannerRenderer.class)
public class TileEntityBannerRendererMixin_BannerAnimation {
    @Redirect(method = "renderTileEntityAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTotalWorldTime()J"))
    private long patcher$resolveOverflow(World world) {
        return world.getTotalWorldTime() % 100L;
    }
}
