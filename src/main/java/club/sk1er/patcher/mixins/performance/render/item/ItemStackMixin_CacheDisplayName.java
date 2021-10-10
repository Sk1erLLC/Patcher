package club.sk1er.patcher.mixins.performance.render.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin_CacheDisplayName {
    private String patcher$cachedDisplayName;

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void patcher$returnCachedDisplayName(CallbackInfoReturnable<String> cir) {
        if (patcher$cachedDisplayName != null) {
            cir.setReturnValue(patcher$cachedDisplayName);
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    private void patcher$cacheDisplayName(CallbackInfoReturnable<String> cir) {
        patcher$cachedDisplayName = cir.getReturnValue();
    }

    @Inject(method = "setStackDisplayName", at = @At("HEAD"))
    private void patcher$resetCachedDisplayName(String displayName, CallbackInfoReturnable<ItemStack> cir) {
        patcher$cachedDisplayName = null;
    }
}
