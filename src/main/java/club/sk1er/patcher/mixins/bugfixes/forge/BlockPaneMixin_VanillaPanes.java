package club.sk1er.patcher.mixins.bugfixes.forge;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Forge introduces <a href="https://github.com/MinecraftForge/MinecraftForge/blob/1.8.9/patches/minecraft/net/minecraft/block/BlockPane.java.patch">a patch</a>
 * which changes the behavior of how panes connect. This mixin (optionally) reverts that change. In vanilla 1.12.2,
 * panes do connect like this so the Forge patch and this mixin are not needed.
 */
@Mixin(BlockPane.class)
public class BlockPaneMixin_VanillaPanes {

    //#if MC==10809
    @Redirect(method = "canPaneConnectTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isSideSolid(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"), remap = false)
    private boolean patcher$vanillaPanes(Block block, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (PatcherConfig.vanillaGlassPanes) {
            return false;
        }
        return block.isSideSolid(world, pos, side);
    }
    //#endif

}
