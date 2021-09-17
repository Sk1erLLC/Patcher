package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEntityItem.class)
public class RenderEntityItemMixin_UnstackedItems {
    @Inject(method = "func_177078_a", at = @At("HEAD"), cancellable = true)
    private void patcher$changeStackType(CallbackInfoReturnable<Integer> cir) {
        if (PatcherConfig.unstackedItems) {
            cir.setReturnValue(1);
        }
    }
}
