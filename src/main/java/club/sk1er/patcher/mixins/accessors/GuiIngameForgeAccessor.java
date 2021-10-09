package club.sk1er.patcher.mixins.accessors;

import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngameForge.class)
public interface GuiIngameForgeAccessor {
    @Invoker(remap = false)
    void callRenderCrosshairs(int width, int height);
}
