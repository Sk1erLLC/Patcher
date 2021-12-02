package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin_ArmPosition {
    @Redirect(method = "renderRightArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPlayer;isSneak:Z", ordinal = 0))
    private void patcher$resetArmState(ModelPlayer modelPlayer, boolean value) {
        modelPlayer.isRiding = modelPlayer.isSneak = false;
    }
}
