package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.achievement.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAchievement.class)
public class GuiAchievementMixin_CancelRender {

    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(CallbackInfo ci) {
        if (PatcherConfig.disableAchievements) ci.cancel();
    }
}
