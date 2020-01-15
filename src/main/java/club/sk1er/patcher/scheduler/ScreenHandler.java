package club.sk1er.patcher.scheduler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScreenHandler {

    private static GuiScreen display;

    public static void open(GuiScreen screen) {
        display = screen;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (display != null) {
            Minecraft.getMinecraft().displayGuiScreen(display);
            display = null;
        }
    }
}
