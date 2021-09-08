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

package club.sk1er.patcher.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WorldHandler {

    public static Vec3 skyColorVector = new Vec3(0.472549021244049, 0.652941197156906, 1);

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            final WorldClient world = Minecraft.getMinecraft().theWorld;
            if (world != null) {
                final float celestialAngle = world.getCelestialAngle(0);
                float modifiedAngle = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
                modifiedAngle = MathHelper.clamp_float(modifiedAngle, 0.0F, 1.0F);
                skyColorVector = new Vec3(0.472549021244049 * modifiedAngle, 0.652941197156906 * modifiedAngle, 1 * modifiedAngle);
            }
        }
    }
}
