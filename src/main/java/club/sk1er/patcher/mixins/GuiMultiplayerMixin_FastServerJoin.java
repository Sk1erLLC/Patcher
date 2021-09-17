package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class GuiMultiplayerMixin_FastServerJoin {

    @Shadow public abstract void selectServer(int index);
    @Shadow public abstract void connectToSelected();

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void patcher$joinServer(CallbackInfo ci) {
        // todo: this can select the lan scan entry
        if (GuiScreen.isCtrlKeyDown()) {
            int eventKey = Keyboard.getEventKey();
            if (eventKey >= Keyboard.KEY_1 && eventKey <= Keyboard.KEY_9) {
                selectServer(eventKey - Keyboard.KEY_1);
                connectToSelected();
            }
        }
    }
}
