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

package club.sk1er.patcher.util.world.render.entity;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.NameTagRenderingHooks;
import club.sk1er.patcher.util.world.render.culling.EntityCulling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class EntityRendering {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRenderer = mc.fontRendererObj;
    private final RenderManager renderManager = mc.getRenderManager();
    private boolean shouldMakeTransparent;

    @SubscribeEvent
    public void cancelRendering(RenderLivingEvent.Pre<? extends EntityLivingBase> event) {
        final EntityLivingBase entity = event.entity;
        if ((PatcherConfig.disableArmorstands && entity instanceof EntityArmorStand) || (PatcherConfig.disableSemitransparentEntities && entity.isInvisible() && entity instanceof EntityPlayer)) {
            event.setCanceled(true);
        }

        final float entityDistance = entity.getDistanceToEntity(mc.thePlayer);
        if (PatcherConfig.entityRenderDistanceToggle && EntityCulling.shouldPerformCulling) {
            if (entityDistance > PatcherConfig.entityRenderDistance) {
                event.setCanceled(true);
            } else if (entity instanceof IMob && entityDistance > PatcherConfig.hostileEntityRenderDistance) {
                event.setCanceled(true);
            } else if ((entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntityWaterMob) && entityDistance > PatcherConfig.passiveEntityRenderDistance) {
                event.setCanceled(true);
            } else if (entity instanceof EntityPlayer && entityDistance > PatcherConfig.playerRenderDistance) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void setHorseTransparentPre(RenderLivingEvent.Pre<EntityHorse> event) {
        if (PatcherConfig.riddenHorseOpacity >= 1.0F) {
            return;
        }

        final Entity ridingEntity = mc.thePlayer.ridingEntity;
        if (ridingEntity == null) {
            return;
        }

        shouldMakeTransparent = ridingEntity == event.entity;
        if (shouldMakeTransparent) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.color(1, 1, 1, PatcherConfig.riddenHorseOpacity);
            GlStateManager.disableDepth();
        }
    }

    @SubscribeEvent
    public void setHorseTransparentPre(RenderLivingEvent.Post<EntityHorse> event) {
        if (shouldMakeTransparent) {
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableDepth();
        }
    }

    /**
     * Render the nametag above our player.
     *
     * @param event {@link RenderLivingEvent.Specials.Pre}
     */
    @SubscribeEvent
    public void renderNametag(RenderLivingEvent.Specials.Pre<EntityPlayerSP> event) {
        if (PatcherConfig.betterHideGui && mc.gameSettings.hideGUI) {
            return;
        }

        final EntityLivingBase entity = event.entity;
        if (entity instanceof EntityPlayerSP
            && !((EntityPlayerSP) entity).isSpectator() && !entity.isInvisible()
            && entity == renderManager.livingPlayer && PatcherConfig.showOwnNametag) {
            renderTag(entity);
        }
    }

    private void renderTag(EntityLivingBase entity) {
        String name = entity.getDisplayName().getFormattedText();
        float nametagScale = 0.016666668F * 1.6f;
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
        GlStateManager.disableTexture2D();

        int stringWidth = fontRenderer.getStringWidth(name) >> 1;
        if (!PatcherConfig.disableNametagBoxes) {
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
        NameTagRenderingHooks.drawNametagText(fontRenderer, name, -stringWidth, 0, entity.isSneaking() ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
