package club.sk1er.patcher.mixins.features;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin_NaturalCapes {
    @Inject(method="setPositionAndRotation", at=@At("HEAD"))
    protected void patcher$overrideMethod(double x, double y, double z, float yaw, float pitch, CallbackInfo ci){
    }
}
