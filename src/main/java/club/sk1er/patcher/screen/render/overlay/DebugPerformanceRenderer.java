package club.sk1er.patcher.screen.render.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;

public class DebugPerformanceRenderer {

    private boolean frameRender = false;
    private long updated = 0;
    private String mode = "???";
    private final int textColor = new Color(1, 165, 82).getRGB();
    private final DecimalFormat format = new DecimalFormat("#.00");
    private final Deque<Long> frames = new ArrayDeque<>();
    private final String[] renderStrings = new String[5];
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int[] intervals = {1, 10, 30, 60};

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !frameRender) return;

        frames.add(System.currentTimeMillis());
        final long currentTime = System.currentTimeMillis();
        while ((currentTime - frames.getFirst()) > 60000) {
            frames.removeFirst();
            if (frames.isEmpty()) break;
        }

        if ((System.currentTimeMillis() - updated) > 1000) {
            updated = System.currentTimeMillis();
            renderStrings[0] = "Mode: " + mode;
            int renderMode = 0;
            for (int interval : intervals) {
                int amt = 0;

                for (long frame : frames) {
                    if ((System.currentTimeMillis() - frame) < (interval * 1000L)) {
                        amt++;
                    }
                }

                renderStrings[++renderMode] = "Avg on " + interval + "s: " + format.format(amt / ((float) interval));
            }
        }

        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        int y = 40;

        for (String render : renderStrings) {
            mc.fontRendererObj.drawString(render, scaledResolution.getScaledWidth() - 5 - mc.fontRendererObj.getStringWidth(render), y, textColor, true);
            y += 10;
        }
    }

    public void toggleFPS() {
        this.frameRender = !this.frameRender;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
