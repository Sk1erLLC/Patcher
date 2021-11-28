package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiSelectWorld.class)
public interface GuiSelectWorldAccessor {
    @Accessor
    //#if MC==10809
    GuiScreen getParentScreen();
    //#else
    //$$ GuiScreen getPrevScreen();
    //#endif
}
