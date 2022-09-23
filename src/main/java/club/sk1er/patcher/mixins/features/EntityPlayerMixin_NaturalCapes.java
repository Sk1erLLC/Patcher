package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin_NaturalCapes extends EntityMixin_NaturalCapes {
    @Shadow
    public double prevChasingPosZ;

    @Shadow
    public double chasingPosX;

    @Shadow
    public double prevChasingPosX;

    @Shadow
    public double prevChasingPosY;

    @Shadow
    public double chasingPosY;

    @Shadow
    public double chasingPosZ;

    @ModifyVariable(method = "onUpdate", ordinal = 3, at = @At("STORE"))
    public double patcher$replaceMaxClampValue(double original) {
        if (PatcherConfig.naturalCapes) {
            return Double.MAX_VALUE;
        }
        return original;
    }

    @Override
    protected void patcher$overrideMethod(double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        if (PatcherConfig.naturalCapes) {
            this.prevChasingPosY = this.chasingPosY = y;
            this.prevChasingPosZ = this.chasingPosZ = z;
            this.prevChasingPosX = this.chasingPosX = x;
        }

    }
}
