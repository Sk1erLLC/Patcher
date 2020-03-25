package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.asm.GuiPlayerTabOverlayTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import org.objectweb.asm.tree.ClassNode;

/** Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)} */
@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

  private static final Minecraft mc = Minecraft.getMinecraft();

  public static void moveTabDownPushMatrix() {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0 && PatcherConfig.tabHeight) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0, 12, 0);
    }
  }

  public static void moveTabDownPopMatrix() {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0 && PatcherConfig.tabHeight) {
      GlStateManager.popMatrix();
    }
  }

  public static int getNewColor(int color) {
    if (!PatcherConfig.customTabOpacity) return color;
    int prevOpacity = Math.abs(color >> 24);
    int opacity = prevOpacity * PatcherConfig.tabOpacity / 100;
    return (opacity << 24) | (color & 0xFFFFFF);
  }

  public static void drawPatcherPing(
      int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo playerInfo) {
    int ping = playerInfo.getResponseTime();
    int x = p_175245_2_ + p_175245_1_ - (mc.fontRendererObj.getStringWidth(ping + "") >> 1) - 2;
    int y = p_175245_3_ + (mc.fontRendererObj.FONT_HEIGHT >> 2);

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

    if (ping >= 0 && ping < 10000) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5f, 0.5f, 0.5f);
      mc.fontRendererObj.drawString("   " + ping + "", (2 * x) - 10, (2 * y), color);
      GlStateManager.scale(2.0f, 2.0f, 2.0f);
      GlStateManager.popMatrix();
    }
  }
}
