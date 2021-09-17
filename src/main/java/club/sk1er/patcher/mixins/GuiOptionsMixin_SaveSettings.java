package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiOptions.class)
public class GuiOptionsMixin_SaveSettings extends GuiScreen {
    @Override
    public void onGuiClosed() {
        mc.gameSettings.saveOptions();
    }
}
