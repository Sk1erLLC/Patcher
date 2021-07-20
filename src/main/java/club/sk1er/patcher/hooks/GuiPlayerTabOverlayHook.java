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

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.asm.render.screen.GuiPlayerTabOverlayTransformer;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)}
 */
public class GuiPlayerTabOverlayHook {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static void moveTabDownPushMatrix() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0 && PatcherConfig.tabHeightAllow) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, PatcherConfig.tabHeight, 0);
        }
    }

    public static void moveTabDownPopMatrix() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0 && PatcherConfig.tabHeightAllow) {
            GlStateManager.popMatrix();
        }
    }

    @SuppressWarnings("unused")
    public static int getNewColor(int color) {
        if (!PatcherConfig.customTabOpacity) return color;
        final int prevOpacity = Math.abs(color >> 24);
        final int opacity = (int) (prevOpacity * PatcherConfig.tabOpacity);
        return (opacity << 24) | (color & 0xFFFFFF);
    }

    @SuppressWarnings("unused")
    public static void drawPatcherPing(int offset, int xPosition, int yPosition, NetworkPlayerInfo info) {
        final int ping = info.getResponseTime();
        final int x = (xPosition + offset) - (mc.fontRendererObj.getStringWidth(String.valueOf(ping)) >> 1) - 2;
        final int y = yPosition + 2;

        int color;

        if (ping > 500) {
            color = 11141120;
        } else if (ping > 300) {
            color = 11184640;
        } else if (ping > 200) {
            color = 11193344;
        } else if (ping > 135) {
            color = 2128640;
        } else if (ping > 70) {
            color = 39168;
        } else if (ping >= 0) {
            color = 47872;
        } else {
            color = 11141120;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        mc.fontRendererObj.drawStringWithShadow("   " + (ping == 0 ? "?" : ping), (2 * x) - 10, 2 * y, color);
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }
}
