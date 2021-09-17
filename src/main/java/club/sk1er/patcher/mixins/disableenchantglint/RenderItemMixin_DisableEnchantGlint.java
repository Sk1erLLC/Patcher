package club.sk1er.patcher.mixins.disableenchantglint;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class RenderItemMixin_DisableEnchantGlint {
    @Inject(method = "renderEffect", at = @At("HEAD"), cancellable = true)
    private void patcher$disableEnchantGlint(CallbackInfo ci) {
        if (PatcherConfig.disableEnchantmentGlint) ci.cancel();
    }
}
