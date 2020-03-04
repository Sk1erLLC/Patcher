package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.asm.GuiPlayerTabOverlayTransformer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import org.objectweb.asm.tree.ClassNode;

/** Used in {@link GuiPlayerTabOverlayTransformer#transform(ClassNode, String)} */
@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

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
}
