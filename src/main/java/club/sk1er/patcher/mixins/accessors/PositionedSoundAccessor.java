package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PositionedSound.class)
public interface PositionedSoundAccessor {
    @Accessor
    void setVolume(float volume);
}
