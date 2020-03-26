package club.sk1er.patcher.util.entity;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class EntityRendering {

  private final Minecraft mc = Minecraft.getMinecraft();
  private final FontRenderer fontRenderer = mc.fontRendererObj;
  private final RenderManager renderManager = mc.getRenderManager();

  @SubscribeEvent
  public void cancelRendering(RenderLivingEvent.Pre<EntityArmorStand> event) {
    if (PatcherConfig.disableArmorstands && event.entity instanceof EntityArmorStand) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void renderNametag(RenderLivingEvent.Specials.Pre<EntityPlayerSP> event) {
    if (event.entity.isEntityEqual(renderManager.livingPlayer) && PatcherConfig.showOwnNametag) {
      renderTag(event.entity, 0, 0, 0);
    }
  }

  private void renderTag(EntityLivingBase entity, double x, double y, double z) {
    String name = entity.getDisplayName().getFormattedText();
    float playerHeight = 1.6f;
    float nametagScale = 0.016666668F * playerHeight;
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y + entity.height + 0.5f, z);
    GL11.glNormal3f(0, 1, 0);
    GlStateManager.rotate(-renderManager.playerViewY, 0, 1, 0);

    int xMultiplier = 1; // Nametag x rotations should flip in front-facing 3rd person

    if (mc != null && mc.gameSettings != null && mc.gameSettings.thirdPersonView == 2) {
      xMultiplier = -1;
    }

    GlStateManager.rotate(renderManager.playerViewX * xMultiplier, 1, 0, 0);
    GlStateManager.scale(-nametagScale, -nametagScale, nametagScale);
    GlStateManager.disableLighting();
    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    int stringWidth = fontRenderer.getStringWidth(name) / 2;
    GlStateManager.disableTexture2D();

    if (!PatcherConfig.transparentNameTags) {
      GlStateManager.color(0, 0, 0, .25F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2d(-stringWidth - 1, -1);
      GL11.glVertex2d(-stringWidth - 1, 8);
      GL11.glVertex2d(stringWidth + 1, 8);
      GL11.glVertex2d(stringWidth + 1, -1);
      GL11.glEnd();
    }

    GlStateManager.enableTexture2D();
    fontRenderer.drawString(name, -stringWidth, 0, 553648127);
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    fontRenderer.drawString(name, -stringWidth, 0, -1);
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }
}
