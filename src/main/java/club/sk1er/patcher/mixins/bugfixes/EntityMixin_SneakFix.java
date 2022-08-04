package club.sk1er.patcher.mixins.bugfixes;

//#if MC==11202
//$$ import net.minecraft.entity.MoverType;
//#endif
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin_SneakFix {
    private double yDisplacement;

    @Inject(method = "moveEntity", at = @At(value = "HEAD"))
    private void moveEntity(
        //#if MC==11202
        //$$ MoverType type,
        //#endif
        double x, double y, double z, CallbackInfo ci) {
        this.yDisplacement = y;
    }

    @Redirect(
        method = "moveEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;onGround:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        )
    )
    private boolean patcher$overrideOnGround(Entity entity) {
        return entity instanceof EntityPlayer
            && !((EntityPlayer) entity).capabilities.isFlying
            && yDisplacement <= 0.0D
            && entity.fallDistance < entity.stepHeight
            && !entity.worldObj.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox().offset(0, -entity.stepHeight, 0)).isEmpty();
    }
}
