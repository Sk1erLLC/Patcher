package club.sk1er.patcher.hooks;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class CacheHooks {
    private long cacheTime;
    public static List<String> tooltipCache;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if ((System.currentTimeMillis() - cacheTime) < 200) return;
            tooltipCache = null;
            cacheTime = System.currentTimeMillis();
        }
    }
}
