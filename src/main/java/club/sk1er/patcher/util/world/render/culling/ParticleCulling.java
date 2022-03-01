package club.sk1er.patcher.util.world.render.culling;

import club.sk1er.patcher.ducks.EntityFXExt;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.culling.ICamera;

public class ParticleCulling {

    public static ICamera camera;

    public static boolean shouldRender(EntityFX entityFX) {
        return entityFX != null && (camera == null || ((EntityFXExt) entityFX).patcher$getCullState() > -1);
    }
}
