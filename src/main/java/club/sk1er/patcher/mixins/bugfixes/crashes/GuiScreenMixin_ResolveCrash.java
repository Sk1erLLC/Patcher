package club.sk1er.patcher.mixins.bugfixes.crashes;

import club.sk1er.patcher.screen.ResolutionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin_ResolveCrash {

    @Shadow public Minecraft mc;

    @SuppressWarnings({"ConstantConditions", "RedundantCast"})
    @Inject(method = "handleInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"), cancellable = true)
    private void patcher$checkScreen(CallbackInfo ci) {
        if ((GuiScreen) (Object) this != this.mc.currentScreen) {
            ResolutionHelper.setScaleOverride(-1);
            ci.cancel();
        }
    }
}
