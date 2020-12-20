package club.sk1er.patcher.hooks.accessors;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IGuiIngameForge {
    void renderCrosshairs(int width, int height);
    boolean pre(RenderGameOverlayEvent.ElementType type);
    void post(RenderGameOverlayEvent.ElementType type);
}
