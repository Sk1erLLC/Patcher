package club.sk1er.patcher.mixins.features.containeropacity;

import club.sk1er.patcher.hooks.ContainerOpacityHook;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiInventory.class)
public class GuiInventoryMixin_ContainerOpacity {
    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/inventory/GuiInventory;drawTexturedModalRect(IIIIII)V"))
    private void patcher$beginContainerOpacity(CallbackInfo ci) {
        ContainerOpacityHook.beginTransparency();
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/inventory/GuiInventory;drawTexturedModalRect(IIIIII)V", shift = At.Shift.AFTER))
    private void patcher$endContainerOpacity(CallbackInfo ci) {
        ContainerOpacityHook.endTransparency();
    }
}
