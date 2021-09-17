package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.GuiGameOver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGameOver.class)
public class GuiGameOverMixin_ResolveButtonClick {
    @Shadow private int enableButtonsTimer;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void patcher$allowClickable(CallbackInfo ci) {
        this.enableButtonsTimer = 0;
    }
}
