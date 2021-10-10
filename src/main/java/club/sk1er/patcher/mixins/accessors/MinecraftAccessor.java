package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("fullscreen")
    void setFullScreen(boolean b);

    @Accessor
    int getTempDisplayWidth();

    @Accessor
    int getTempDisplayHeight();

    @Invoker
    void callUpdateFramebufferSize();
}
