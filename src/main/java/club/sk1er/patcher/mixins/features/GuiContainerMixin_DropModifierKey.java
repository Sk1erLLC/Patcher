package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainer.class)
public class GuiContainerMixin_DropModifierKey {
    @Redirect(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;isCtrlKeyDown()Z"))
    private boolean patcher$useModifierKey() {
        return GuiScreen.isCtrlKeyDown() || GameSettings.isKeyDown(Patcher.instance.getDropModifier());
    }
}
