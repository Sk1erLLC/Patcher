package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEntityItem.class)
public class RenderEntityItemMixin_StaticItems {
    @Inject(method = "shouldBob", at = @At("HEAD"), cancellable = true, remap = false)
    private void patcher$checkOption(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!PatcherConfig.staticItems);
    }
}
