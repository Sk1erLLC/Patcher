package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityBannerRenderer.class)
public class TileEntityBannerRendererMixin_BannerAnimation {

    private final String patcher$renderTileEntityAtDesc =
        //#if MC==10809
        "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityBanner;DDDFI)V";
        //#else
        //$$ "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityBanner;DDDFIF)V";
        //#endif

    @Redirect(method = patcher$renderTileEntityAtDesc, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTotalWorldTime()J"))
    private long patcher$resolveOverflow(World world) {
        return world.getTotalWorldTime() % 100L;
    }
}
