package club.sk1er.patcher.util.world.render.culling;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.culling.ICamera;

@SuppressWarnings("unused")
public class ParticleCulling {

    public static ICamera camera;

    public static boolean shouldRender(EntityFX particle) {
        return particle != null && camera != null && camera.isBoundingBoxInFrustum(
            //#if MC==10809
            particle.getEntityBoundingBox()
            //#else
            //$$ particle.getBoundingBox()
            //#endif
        );
    }
}
