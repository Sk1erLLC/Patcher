package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class GuiContainerMixin_MouseBindFix extends GuiScreen {

    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void patcher$checkCloseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton - 100 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void patcher$checkHotbarClicks(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        // Labymod does the same thing, and now causes this to not work at all. Don't run if Labymod is present.
        if (!Loader.isModLoaded("labymod")) checkHotbarKeys(mouseButton - 100);
    }
}
