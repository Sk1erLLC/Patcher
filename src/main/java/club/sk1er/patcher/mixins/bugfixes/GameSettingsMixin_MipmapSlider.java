package club.sk1er.patcher.mixins.bugfixes;

import club.sk1er.patcher.ducks.GameSettingsExt;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameSettings.class)
public class GameSettingsMixin_MipmapSlider implements GameSettingsExt {
    @Shadow protected Minecraft mc;

    private boolean patcher$needsResourceRefresh;

    @Override
    public void patcher$onSettingsGuiClosed() {
        if (patcher$needsResourceRefresh) {
            mc.scheduleResourcesRefresh();
            patcher$needsResourceRefresh = false;
        }
    }

    //#if MC==10809
    @Redirect(
        method = "setOptionFloatValue",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;scheduleResourcesRefresh()Lcom/google/common/util/concurrent/ListenableFuture;")
    )
    private ListenableFuture<Object> patcher$scheduleResourceRefresh(Minecraft instance) {
        patcher$needsResourceRefresh = true;
        return null;
    }
    //#endif
}
