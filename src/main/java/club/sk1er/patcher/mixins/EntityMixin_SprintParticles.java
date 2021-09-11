package club.sk1er.patcher.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin_SprintParticles {

    @Shadow public boolean onGround;

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void patcher$checkGroundState(CallbackInfo ci) {
        if (!this.onGround) ci.cancel();
    }
}
