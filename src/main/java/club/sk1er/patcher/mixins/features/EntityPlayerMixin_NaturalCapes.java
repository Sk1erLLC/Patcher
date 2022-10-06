package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin_NaturalCapes {
    @ModifyVariable(method = "onUpdate", ordinal = 3, at = @At("STORE"))
    public double onUpdate(double ori) {
        if (PatcherConfig.naturalCapes) {
            return 999999.0D;
        }
        return ori;
    }
}
