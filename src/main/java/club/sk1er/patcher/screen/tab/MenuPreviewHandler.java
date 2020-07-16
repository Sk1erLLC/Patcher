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

package club.sk1er.patcher.screen.tab;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class MenuPreviewHandler {

    private boolean toggledTab;
    private boolean toggledChat;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent event) {
        if (Patcher.instance.getChatPeek().isPressed()) {
            this.toggledChat = !this.toggledChat;
        } else if (mc.gameSettings.keyBindPlayerList.isPressed()) {
            this.toggledTab = !this.toggledTab;
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (!event.type.equals(RenderGameOverlayEvent.ElementType.ALL)) return;


        WorldClient world = mc.theWorld;
        ScaledResolution resolution = new ScaledResolution(mc);

        int scaledWidth = resolution.getScaledWidth();
        int scaledHeight = resolution.getScaledHeight();

        GuiNewChat chat = this.mc.ingameGUI.getChatGUI();
        if (this.toggledChat && !chat.getChatOpen()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, (PatcherConfig.chatPosition ? (float) (scaledHeight - 60) : (float) (scaledHeight - 48)), 0.0F);
            chat.drawChat(0);
            GlStateManager.popMatrix();
        }

        Scoreboard scoreboard = world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);

        if (PatcherConfig.toggleTab && this.toggledTab && !mc.gameSettings.keyBindPlayerList.isKeyDown()) {
            if (mc.isIntegratedServerRunning() && mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && objective == null) {
                mc.ingameGUI.getTabList().updatePlayerList(false);
            } else {
                mc.ingameGUI.getTabList().updatePlayerList(true);
                mc.ingameGUI.getTabList().renderPlayerlist(scaledWidth, scoreboard, objective);
            }
        }
    }
}
