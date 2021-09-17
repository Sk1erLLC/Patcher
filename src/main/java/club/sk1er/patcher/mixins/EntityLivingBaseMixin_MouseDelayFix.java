package club.sk1er.patcher.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_MouseDelayFix extends Entity {
    public EntityLivingBaseMixin_MouseDelayFix(World worldIn) {
        super(worldIn);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    private void patcher$mouseDelayFix(float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
            cir.setReturnValue(super.getLook(partialTicks));
        }
    }
}
