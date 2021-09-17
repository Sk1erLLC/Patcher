package club.sk1er.patcher.mixins.crashes;

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
            // todo: make GuiScreenMixin_InventoryScale automatically inject this
            //  i'm unsure as to how to make it so that mixin can apply to this, as changing it's value to RETURN
            //  does not make it do that (though it should inject at any return?)
            ResolutionHelper.setScaleOverride(-1);
            ci.cancel();
        }
    }
}
