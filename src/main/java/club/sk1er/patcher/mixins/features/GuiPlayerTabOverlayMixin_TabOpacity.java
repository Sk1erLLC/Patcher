package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin_TabOpacity {

    @ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = -2147483648))
    private int patcher$modifyColor(int original) {
        return this.patcher$modifiedColor(original);
    }

    @ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 553648127))
    private int patcher$modifyColor2(int original) {
        return this.patcher$modifiedColor(original);
    }

    private int patcher$modifiedColor(int color) {
        if (!PatcherConfig.customTabOpacity) return color;
        int prevOpacity = Math.abs(color >> 24);
        int opacity = (int) (prevOpacity * PatcherConfig.tabOpacity);
        return (opacity << 24) | (color & 0xFFFFFF);
    }
}
