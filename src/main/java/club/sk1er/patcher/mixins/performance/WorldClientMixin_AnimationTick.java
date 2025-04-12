package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldClient.class)
public class WorldClientMixin_AnimationTick {
    @ModifyConstant(method = "doVoidFogParticles", constant = @Constant(intValue = 1000))
    private int patcher$lowerTickCount(int original) {
        return PatcherConfig.lowAnimationTick ? 20 : original;
    }
}
