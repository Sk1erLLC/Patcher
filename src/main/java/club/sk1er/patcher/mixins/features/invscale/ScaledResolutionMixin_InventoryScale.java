package club.sk1er.patcher.mixins.features.invscale;

import club.sk1er.patcher.screen.ResolutionHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScaledResolution.class)
public class ScaledResolutionMixin_InventoryScale {
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;guiScale:I", opcode = Opcodes.GETFIELD))
    private int patcher$modifyScale(GameSettings gameSettings) {
        int scale = ResolutionHelper.getScaleOverride();
        return scale >= 0 ? scale : gameSettings.guiScale;
    }
}
