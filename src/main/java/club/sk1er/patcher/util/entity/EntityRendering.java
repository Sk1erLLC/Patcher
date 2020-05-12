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

package club.sk1er.patcher.util.entity;

import club.sk1er.patcher.config.PatcherConfig;

import java.lang.ref.WeakReference;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.opengl.GL11;

public class EntityRendering {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRenderer = mc.fontRendererObj;
    private final RenderManager renderManager = mc.getRenderManager();
    private final String key = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";
    private WeakReference<EntityLivingBase> reference;

    @SubscribeEvent
    public void cancelRendering(RenderLivingEvent.Pre<EntityArmorStand> event) {
        if (PatcherConfig.disableArmorstands && event.entity instanceof EntityArmorStand) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void renderNametag(RenderLivingEvent.Specials.Pre<EntityPlayerSP> event) {
        if (event.entity.isEntityEqual(renderManager.livingPlayer) && PatcherConfig.showOwnNametag) {
            renderTag(event.entity);
        }
    }

    @SubscribeEvent
    public void tickClient(ClientTickEvent event) {
        if (!PatcherConfig.cleanView) return;

        if (event.phase == Phase.START) {
            EntityLivingBase entity = (EntityLivingBase) mc.getRenderViewEntity();
            EntityLivingBase previousEntity = (reference != null) ? reference.get() : null;
            if (previousEntity != entity) {
                if (previousEntity != null && previousEntity.getEntityData().getBoolean(key)) {
                    Collection<PotionEffect> effects = previousEntity.getActivePotionEffects();
                    if (!effects.isEmpty()) {
                        previousEntity.getDataWatcher().updateObject(7, PotionHelper.calcPotionLiquidColor(effects));
                    }

                    previousEntity.getEntityData().removeTag(key);
                }

                reference = (entity != null) ? new WeakReference<>(entity) : null;
            }

            if (entity != null) {
                entity.getDataWatcher().updateObject(7, 0);
                if (!entity.getEntityData().getBoolean(key)) {
                    entity.getEntityData().setBoolean(key, true);
                }
            }
        }
    }

    private void renderTag(EntityLivingBase entity) {
        String name = entity.getDisplayName().getFormattedText();
        float playerHeight = 1.6f;
        float nametagScale = 0.016666668F * playerHeight;
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, entity.height + 0.5f, 0);
        GL11.glNormal3f(0, 1, 0);
        GlStateManager.rotate(-renderManager.playerViewY, 0, 1, 0);

        int xMultiplier = 1; // Nametag x rotations should flip in front-facing 3rd person

        if (mc != null && mc.gameSettings != null && mc.gameSettings.thirdPersonView == 2) {
            xMultiplier = -1;
        }

        GlStateManager.rotate(renderManager.playerViewX * xMultiplier, 1, 0, 0);
        GlStateManager.scale(-nametagScale, -nametagScale, nametagScale);

        if (entity.isSneaking()) {
            GlStateManager.translate(0, 9.374999f, 0);
        }

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
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        if (entity.isSneaking()) {
            fontRenderer.drawString(name, -stringWidth, 0, 553648127);
        } else {
            fontRenderer.drawString(name, -stringWidth, 0, -1);
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
