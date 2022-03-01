package club.sk1er.patcher.mixins.features.lefthand;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderFish.class)
public class RenderFishMixin_LeftHandedness {
    @ModifyVariable(method = "doRender(Lnet/minecraft/entity/projectile/EntityFishHook;DDDFF)V", at = @At(value = "STORE", ordinal = 0))
    private Vec3 patcher$flipFishingLine(Vec3 original) {
        if (PatcherConfig.leftHandInFirstPerson) {
            return new Vec3(-original.xCoord, original.yCoord, original.zCoord);
        }
        return original;
    }
}
