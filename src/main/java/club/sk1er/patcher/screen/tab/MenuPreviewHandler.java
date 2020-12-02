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
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class MenuPreviewHandler {

    private boolean toggledTab;
    private boolean toggledChat;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent event) {
        if (mc.gameSettings.keyBindPlayerList.isPressed()) {
            this.toggledTab = !this.toggledTab;
        }
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        this.toggledChat = Patcher.instance.getChatPeek().isKeyDown();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        final ScaledResolution resolution = new ScaledResolution(mc);
        final int scaledWidth = resolution.getScaledWidth();
        final int scaledHeight = resolution.getScaledHeight();

        final GuiNewChat chat = mc.ingameGUI.getChatGUI();
        if (this.toggledChat && !chat.getChatOpen()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, (float) (scaledHeight - (PatcherConfig.chatPosition ? 60 : 48)), 0.0F);
            chat.drawChat(0);
            GlStateManager.popMatrix();
        }

        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        final ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);

        if (PatcherConfig.toggleTab && this.toggledTab && !mc.gameSettings.keyBindPlayerList.isKeyDown()) {
            final GuiPlayerTabOverlay tabOverlay = mc.ingameGUI.getTabList();
            if (mc.isIntegratedServerRunning() && mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && objective == null) {
                tabOverlay.updatePlayerList(false);
            } else {
                tabOverlay.updatePlayerList(true);
                tabOverlay.renderPlayerlist(scaledWidth, scoreboard, objective);
            }
        }
    }
}
