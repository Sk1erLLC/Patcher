package club.sk1er.patcher.mixins.accessors;

import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityArrow.class)
public interface EntityArrowAccessor {
    @Accessor
    boolean getInGround();
}
