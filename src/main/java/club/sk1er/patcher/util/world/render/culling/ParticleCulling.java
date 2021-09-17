package club.sk1er.patcher.util.world.render.culling;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.culling.ICamera;

@SuppressWarnings("unused")
public class ParticleCulling {

    public static ICamera camera;

    public static boolean shouldRender(EntityFX entityFX) {
        return entityFX != null && (camera == null || entityFX.distanceWalkedModified > -1);
    }
}
