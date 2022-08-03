package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin_SneakFix {
    @Shadow
    public boolean onGround;

    @Shadow
    public World worldObj;

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Redirect(
        method = "moveEntity",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/Entity;onGround:Z",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        )
    )
    private boolean patcher$overrideOnGround(Entity instance) {
        return !this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().offset(0, -1.0, 0)).isEmpty();
    }
}
