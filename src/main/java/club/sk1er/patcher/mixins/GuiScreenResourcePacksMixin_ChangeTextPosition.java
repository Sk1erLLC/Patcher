package club.sk1er.patcher.mixins;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiScreenResourcePacks.class)
public class GuiScreenResourcePacksMixin_ChangeTextPosition {
    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 77))
    private int patcher$moveInformationText(int original) {
        return !Loader.isModLoaded("ResourcePackOrganizer") ? 102 : original;
    }
}
