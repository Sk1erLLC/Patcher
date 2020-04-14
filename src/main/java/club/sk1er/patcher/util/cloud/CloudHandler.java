package club.sk1er.patcher.util.cloud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CloudHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final CloudRenderer renderer = new CloudRenderer();

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        renderer.checkSettings();
    }

    public boolean renderClouds(int cloudTicks, float partialTicks) {
        IRenderHandler renderHandler = mc.theWorld.provider.getCloudRenderer();
        if (renderHandler != null) {
            renderHandler.render(partialTicks, mc.theWorld, mc);
            return true;
        }

        return renderer.render(cloudTicks, partialTicks);
    }

    public CloudRenderer getRenderer() {
        return renderer;
    }
}
