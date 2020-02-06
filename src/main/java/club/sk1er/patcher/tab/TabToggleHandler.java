package club.sk1er.patcher.tab;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class TabToggleHandler {

    private boolean toggled;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent event) {
        if (!mc.gameSettings.keyBindPlayerList.isPressed()) return;
        toggled = !toggled;
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (!event.type.equals(RenderGameOverlayEvent.ElementType.ALL)) return;

        WorldClient world = mc.theWorld;
        ScaledResolution resolution = new ScaledResolution(mc);

        Scoreboard scoreboard = world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);

        if (PatcherConfig.toggleTab && toggled && !mc.gameSettings.keyBindPlayerList.isKeyDown()) {
            if (mc.isIntegratedServerRunning() && mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && objective == null) {
                mc.ingameGUI.getTabList().updatePlayerList(false);
            } else {
                mc.ingameGUI.getTabList().updatePlayerList(true);
                mc.ingameGUI.getTabList().renderPlayerlist(resolution.getScaledWidth(), scoreboard, objective);
            }
        }
    }
}
