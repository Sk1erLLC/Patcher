package club.sk1er.patcher.mixins.features;

// todo: make this work in 1.12
//#if MC==10809
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiIngame.class)
public class GuiIngameMixin_CrosshairVisibility {

    @Shadow
    @Final
    protected Minecraft mc;

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void patcher$checkStates(CallbackInfoReturnable<Boolean> cir) {
        if ((PatcherConfig.guiCrosshair && mc.currentScreen != null) || (PatcherConfig.crosshairPerspective && mc.gameSettings.thirdPersonView != 0)) {
            cir.setReturnValue(false);
        }
    }
}
//#endif
