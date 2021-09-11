package club.sk1er.patcher.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin_FixedBrightness {

    @Shadow public double posX;
    @Shadow public double posY;
    @Shadow public abstract float getEyeHeight();
    @Shadow public double posZ;
    @Shadow public World worldObj;

    /**
     * @author asbyth
     * @reason Resolve entity & particle lighting past y=256
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getBrightnessForRender(float partialTicks) {
        BlockPos blockpos = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
        return this.worldObj.getCombinedLight(blockpos, 0);
    }
}
