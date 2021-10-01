package club.sk1er.patcher.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_BreakParticles {
    @Redirect(
        method = "sendClickBlockToController",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;isUsingItem()Z"
        )
    )
    public boolean patcher$breakParticleSpectator(EntityPlayerSP entityPlayerSP) {
        return entityPlayerSP.isUsingItem() && entityPlayerSP.isSpectator();
    }
}
