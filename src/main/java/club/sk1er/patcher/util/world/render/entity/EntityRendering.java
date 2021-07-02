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
}
