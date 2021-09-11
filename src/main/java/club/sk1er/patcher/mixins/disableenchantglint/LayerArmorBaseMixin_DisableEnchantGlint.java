package club.sk1er.patcher.mixins.disableenchantglint;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class LayerArmorBaseMixin_DisableEnchantGlint {
    @Inject(method = "renderGlint", at = @At("HEAD"), cancellable = true)
    private void patcher$disableEnchantGlint(CallbackInfo ci) {
        if (PatcherConfig.disableEnchantmentGlint) ci.cancel();
    }
}
