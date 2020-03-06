package club.sk1er.patcher.util;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityRendering {

  @SubscribeEvent
  public void cancelRendering(RenderLivingEvent.Pre<EntityArmorStand> event) {
    if (PatcherConfig.disableArmorstands && event.entity instanceof EntityArmorStand) {
      event.setCanceled(true);
    }
  }
}
