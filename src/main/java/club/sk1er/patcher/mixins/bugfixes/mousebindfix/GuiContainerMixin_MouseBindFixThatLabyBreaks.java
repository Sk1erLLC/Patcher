package club.sk1er.patcher.mixins.bugfixes.mousebindfix;

import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Labymod breaks this, don't apply when they're present
@Mixin(GuiContainer.class)
public abstract class GuiContainerMixin_MouseBindFixThatLabyBreaks {
    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void patcher$checkHotbarClicks(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        checkHotbarKeys(mouseButton - 100);
    }
}
