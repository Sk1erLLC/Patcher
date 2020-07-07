/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.world.particles;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.culling.ICamera;

@SuppressWarnings("unused")
public class ParticleCulling {

    public static ICamera camera;

    public static boolean shouldRender(EntityFX entityFX) {
        if (!PatcherConfig.cullParticles) return true;
        if (entityFX == null) return false;
        return camera == null || entityFX.distanceWalkedModified > -1;
    }
}
