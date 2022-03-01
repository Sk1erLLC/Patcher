package club.sk1er.patcher.mixins.features;

import net.minecraft.client.gui.*;
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

    @Shadow
    private ServerSelectionList serverListSelector;

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void patcher$joinServer(CallbackInfo ci) {
        if (GuiScreen.isCtrlKeyDown()) {
            int eventKey = Keyboard.getEventKey();
            if (eventKey >= Keyboard.KEY_1 && eventKey <= Keyboard.KEY_9) {
                int index = eventKey - Keyboard.KEY_1;
                // if index is too high this would normally cause a crash, but ServerSelectionListMixin_ResolveCrash
                // prevents this, and this is more convenient, so we'll just do it like this
                GuiListExtended.IGuiListEntry entry = serverListSelector.getListEntry(index);
                if (entry instanceof ServerListEntryLanScan) return;

                selectServer(index);
                connectToSelected();
            }
        }
    }
}
