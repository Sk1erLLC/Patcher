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

package club.sk1er.patcher.util.world.render.cloud;

import club.sk1er.patcher.asm.render.world.RenderGlobalTransformer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used for checking the current users settings for if
 * they have clouds enabled, on fancy, on fast, and rendering the clouds.
 */
public class CloudHandler {

    /**
     * Create a Minecraft instance.
     */
    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Create a cloud renderer instance.
     */
    private final CloudRenderer renderer = new CloudRenderer();

    /**
     * Ran every client tick, checks the current game settings.
     *
     * @param event {@link TickEvent.ClientTickEvent}
     */
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        renderer.checkSettings();
    }

    /**
     * Render our much faster cloud renderer.
     * Suppressed unused as it's used in ASM {@link RenderGlobalTransformer#transform(ClassNode, String)}.
     *
     * @param cloudTicks   Current cloud ticks.
     * @param partialTicks Current world ticks.
     * @return Returns true if our cloud renderer is ready to render.
     */
    @SuppressWarnings("unused")
    public boolean renderClouds(int cloudTicks, float partialTicks) {
        IRenderHandler renderHandler = mc.theWorld.provider.getCloudRenderer();
        if (renderHandler != null) {
            renderHandler.render(partialTicks, mc.theWorld, mc);
            return true;
        }

        return renderer.render(cloudTicks, partialTicks);
    }

    /**
     * Allow for accessing our cloud renderer.
     *
     * @return The cloud renderer instance.
     */
    public CloudRenderer getRenderer() {
        return renderer;
    }
}
