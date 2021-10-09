package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiMainMenu.class)
public interface GuiMainMenuAccessor {
    @Accessor
    GuiButton getRealmsButton();
}
