package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainer.class)
public class GuiContainerMixin_ClickOutOfContainers {
    @Redirect(method = "mouseClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;touchscreen:Z", ordinal = 0))
    private boolean patcher$checkSetting(GameSettings instance) {
        return PatcherConfig.clickOutOfContainers || instance.touchscreen;
    }
}
