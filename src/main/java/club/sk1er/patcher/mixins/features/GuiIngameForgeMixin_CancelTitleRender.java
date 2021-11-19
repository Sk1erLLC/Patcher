package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_CancelTitleRender {
    @Inject(method = "renderTitle", at = @At("HEAD"),cancellable = true, remap = false)
    private void patcher$cancelTitleRender(CallbackInfo ci) {
        if (PatcherConfig.disableTitles) ci.cancel();
    }
}
