package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Invoker
    float callGetSoundCategoryVolume(SoundCategory category);
}
