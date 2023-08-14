package club.sk1er.patcher.mixins.bugfixes.forge;

import club.sk1er.patcher.ducks.BlockExt;
import net.minecraft.block.BlockChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockChest.class)
public class BlockChestMixin_VanillaSlabChests {
    //#if MC<11200
    @Redirect(method = "isBelowSolidBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSideSolid(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z"))
    public boolean patcher$isBelowSolidBlock(World instance, BlockPos blockPos, EnumFacing enumFacing, boolean b) {
        return ((BlockExt) instance.getBlockState(blockPos).getBlock()).patcher$doesSideBlockChestOpening(instance, blockPos, enumFacing);
    }
    //#endif
}
