package club.sk1er.patcher.mixins.bugfixes.crashes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

//#if MC==11202
//$$ import net.minecraft.potion.Potion;
//#endif

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin_ResolveCrash {
    @Inject(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;onUpdate(Lnet/minecraft/entity/EntityLivingBase;)Z"),
        locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void patcher$checkPotionEffect(CallbackInfo ci,
                                           //#if MC==10809
                                           Iterator<Integer> iterator, Integer integer,
                                           //#else
                                           //$$ Iterator<Potion> iterator, Potion potion,
                                           //#endif
                                           PotionEffect potioneffect) {
        if (potioneffect == null) {
            ci.cancel();
        }
    }
}
