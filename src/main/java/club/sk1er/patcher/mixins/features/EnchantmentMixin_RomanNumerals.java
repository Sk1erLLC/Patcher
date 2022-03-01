package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.RomanNumerals;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin_RomanNumerals {

    @Shadow
    public abstract String getName();

    @Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
    private void patcher$modifyRomanNumerals(int level, CallbackInfoReturnable<String> cir) {
        String translation = StatCollector.translateToLocal(this.getName()) + " ";
        if (PatcherConfig.numericalEnchants) {
            cir.setReturnValue(translation + level);
        } else if (PatcherConfig.betterRomanNumerals) {
            cir.setReturnValue(translation + RomanNumerals.toRoman(level));
        }
    }
}
