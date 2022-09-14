package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundHandler.class)
public interface SoundHandlerAccessor {
    @Accessor
    SoundRegistry getSndRegistry();

    @Accessor
    SoundManager getSndManager();
}
