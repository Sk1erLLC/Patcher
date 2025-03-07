package club.sk1er.patcher.mixins.bugfixes.forge;

import club.sk1er.patcher.ducks.BlockExt;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin_VanillaSlabChests implements BlockExt {
    //#if MC<11200
    @Shadow(remap = false)
    public abstract String getRegistryName();

    @Shadow
    public abstract boolean isNormalCube();

    @Shadow(remap = false)
    public abstract boolean isSideSolid(IBlockAccess par1, BlockPos par2, EnumFacing par3);

    public boolean patcher$doesSideBlockChestOpening(IBlockAccess world, BlockPos pos, EnumFacing side) {
        String registryName = this.getRegistryName();
        if (registryName != null && registryName.equals("minecraft")) {
            // maintain the vanilla behavior of https://bugs.mojang.com/browse/MC-378
            return isNormalCube();
        }
        return isSideSolid(world, pos, side);
    }
    //#endif
}
