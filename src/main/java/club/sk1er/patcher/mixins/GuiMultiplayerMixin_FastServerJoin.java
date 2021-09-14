package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_1:
                    patcher$connectToServer(0);
                    break;
                case Keyboard.KEY_2:
                    patcher$connectToServer(1);
                    break;
                case Keyboard.KEY_3:
                    patcher$connectToServer(2);
                    break;
                case Keyboard.KEY_4:
                    patcher$connectToServer(3);
                    break;
                case Keyboard.KEY_5:
                    patcher$connectToServer(4);
                    break;
                case Keyboard.KEY_6:
                    patcher$connectToServer(5);
                    break;
                case Keyboard.KEY_7:
                    patcher$connectToServer(6);
                    break;
                case Keyboard.KEY_8:
                    patcher$connectToServer(7);
                    break;
                case Keyboard.KEY_9:
                    patcher$connectToServer(8);
                    break;
                default:
                    break;
            }
        }
    }

    @Unique
    private void patcher$connectToServer(int index) {
        selectServer(index);
        connectToSelected();
    }
}
