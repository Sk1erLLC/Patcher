package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityXPOrb.class)
public class EntityXPOrbMixin_AdjustHeight {
    //#if MC==10809
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getEyeHeight()F"))
    private float patcher$lowerHeight(EntityPlayer entityPlayer) {
        return (float) (entityPlayer.getEyeHeight() / 2.0D);
    }
    //#endif
}
