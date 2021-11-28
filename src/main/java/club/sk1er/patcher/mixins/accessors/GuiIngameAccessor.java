package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngame.class)
public interface GuiIngameAccessor {
    @Accessor
    String getDisplayedTitle();

    @Accessor
    String getDisplayedSubTitle();

    @Accessor
    void setDisplayedTitle(String title);

    @Accessor
    void setDisplayedSubTitle(String subTitle);

    //#if MC==10809
    @Invoker
    boolean invokeShowCrosshair();
    //#endif
}
