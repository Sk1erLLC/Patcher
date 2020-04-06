package club.sk1er.patcher.util.culling;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityCulling {

  private final Minecraft mc = Minecraft.getMinecraft();

  @SubscribeEvent
  public void stopRenderingItems(RenderItemInFrameEvent event) {
    if (PatcherConfig.itemFrameCulling && mc.thePlayer != null && !mc.thePlayer
        .canEntityBeSeen(event.entityItemFrame)) {
      event.setCanceled(true);
    }
  }
}
