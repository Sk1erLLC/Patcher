package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.model.TexturedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TexturedQuad.class)
public interface TexturedQuadAccessor {
    @Accessor
    boolean isInvertNormal();
}
