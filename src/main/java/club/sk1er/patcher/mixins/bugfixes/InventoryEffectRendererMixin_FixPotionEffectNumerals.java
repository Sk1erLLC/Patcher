package club.sk1er.patcher.mixins.bugfixes;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.RomanNumerals;
import gg.essential.lib.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryEffectRenderer.class)
public class InventoryEffectRendererMixin_FixPotionEffectNumerals {

    private int patcher$potionAmplifierLevel;

    @ModifyExpressionValue(
        method = "drawActivePotionEffects",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getAmplifier()I", ordinal = 0)
    )
    private int patcher$skipOriginalCode(int amplifier) {
        if (PatcherConfig.betterRomanNumerals) {
            this.patcher$potionAmplifierLevel = amplifier;
            return 1;
        }
        return amplifier;
    }

    @ModifyExpressionValue(
        method = "drawActivePotionEffects",
        at =
        @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
            ordinal = 1)
    )
    private String patcher$addRomanNumeral(String string) {
        if (PatcherConfig.betterRomanNumerals) {
            if (this.patcher$potionAmplifierLevel > 0) {
                return RomanNumerals.toRoman(this.patcher$potionAmplifierLevel + 1);
            }
            return "";
        }
        return string;
    }

}
