package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.screen.FakeMultiplayerMenu;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenServerList.class)
public class GuiScreenServerListMixin_FakeServerListDirectConnect {
    @Shadow @Final private GuiScreen field_146303_a;

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void patcher$disconnectIfNecessary(GuiButton button, CallbackInfo ci) {
        if (button.enabled && button.id == 0 && this.field_146303_a instanceof FakeMultiplayerMenu) {
            FakeMultiplayerMenu parentScreen = (FakeMultiplayerMenu) this.field_146303_a;
            parentScreen.performDisconnection();
        }
    }
}
