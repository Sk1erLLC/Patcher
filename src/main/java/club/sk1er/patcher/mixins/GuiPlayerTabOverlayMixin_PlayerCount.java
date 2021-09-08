package club.sk1er.patcher.mixins;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin_PlayerCount {
    @ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 80))
    private int patcher$changePlayerCount(int original) {
        return PatcherConfig.tabPlayerCount;
    }
}
