package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.GuiScreenOptionsSounds$Button")
public class GuiScreenOptionsSoundsMixin_PacketSpam {

    // don't send a packet for every frame the slider is dragged, instead save that for when the slider is released
    @Redirect(method = "mouseDragged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V"))
    private void patcher$cancelSaving(GameSettings instance) {
        // no-op
    }

    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;playSound(Lnet/minecraft/client/audio/ISound;)V"))
    private void patcher$save(int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }
}
