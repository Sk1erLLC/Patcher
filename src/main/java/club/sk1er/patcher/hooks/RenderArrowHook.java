package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.EntityArrowAccessor;
import net.minecraft.entity.projectile.EntityArrow;

public class RenderArrowHook {
    public static boolean cancelRendering(EntityArrow entity) {
        boolean grounded = ((EntityArrowAccessor) entity).getInGround();
        boolean moving = entity.motionX > 0 || entity.motionY > 0 || entity.motionZ > 0;
        return (PatcherConfig.disableMovingArrows && moving && !grounded) || (PatcherConfig.disableGroundedArrows && grounded);
    }
}
