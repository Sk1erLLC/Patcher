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

package club.sk1er.patcher.util.world.entity.culling;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Used for stopping entities from rendering if they are not visible to the player
 * Subsequent entity on entity occlusion derived from https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
 */
public class EntityCulling {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final HashMap<UUID, OcclusionQuery> queries = new HashMap<>();
    private static final boolean SUPPORT_NEW_GL = GLContext.getCapabilities().OpenGL33;
    public static boolean uiRendering = false;

    /**
     * Used for checking if the entities nametag can be rendered if the user still wants
     * to see nametags despite the entity being culled.
     * <p>
     * Mirrored from {@link RendererLivingEntity} as it's originally protected.
     *
     * @param entity The entity that's being culled.
     * @return The status on if the nametag is liable for rendering.
     */
    public static boolean canRenderName(EntityLivingBase entity) {
        EntityPlayerSP player = mc.thePlayer;
        if (entity instanceof EntityPlayer && entity != player) {
            Team otherEntityTeam = entity.getTeam();
            Team playerTeam = player.getTeam();

            if (otherEntityTeam != null) {
                Team.EnumVisible teamVisibilityRule = otherEntityTeam.getNameTagVisibility();

                switch (teamVisibilityRule) {
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return playerTeam == null || otherEntityTeam.isSameTeam(playerTeam);
                    case HIDE_FOR_OWN_TEAM:
                        return playerTeam == null || !otherEntityTeam.isSameTeam(playerTeam);
                    case ALWAYS:
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled()
            && entity != mc.getRenderManager().livingPlayer
            && !entity.isInvisibleToPlayer(player)
            && entity.riddenByEntity == null;
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB b) {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(false, false, false, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();

        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
    }

    /*
        Used by  RenderEntityItemTransformer and RenderItemFrameTransformer
     */
    @SuppressWarnings("unused")
    public static boolean renderItem(Entity stack) {
        //needs to be called from RenderEntityItem#doRender and RenderItemFrame#doRender. Returning true means it should cancel the render event
        return !uiRendering && PatcherConfig.entityCulling && checkEntity(stack);
    }

    private static int getQuery() {
        return GL15.glGenQueries();
    }

    /**
     * Used OpenGL queries in order to determine if the given is visible
     *
     * @param entity entity to check
     * @return true if the entity rendering should be skipped
     */
    private static boolean checkEntity(Entity entity) {
        OcclusionQuery query = queries.computeIfAbsent(entity.getUniqueID(), OcclusionQuery::new);

        if (query.refresh) {
            query.nextQuery = getQuery();
            query.refresh = false;
            GlStateManager.pushMatrix();
            final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            GlStateManager.translate(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ);
            final int mode = SUPPORT_NEW_GL ? GL33.GL_ANY_SAMPLES_PASSED : GL15.GL_SAMPLES_PASSED;
            GL15.glBeginQuery(mode, query.nextQuery);
            drawSelectionBoundingBox(entity.getEntityBoundingBox().expand(.2, .2, .2));
            GL15.glEndQuery(mode);
            GlStateManager.popMatrix();
        }

        return query.occluded;
    }

    /**
     * Fire rays from the player's eyes, detecting on if it can see an entity or not.
     * If it can see an entity, continue to render the entity, otherwise save some time
     * performing rendering and cancel the entity render.
     *
     * @param event {@link RenderLivingEvent.Pre<EntityLivingBase>}
     */
    @SubscribeEvent
    public void shouldRenderEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!PatcherConfig.entityCulling || uiRendering) {
            return;
        }


        final EntityLivingBase entity = event.entity;
        if (checkEntity(entity)) {
            event.setCanceled(true);
            if (!canRenderName(entity)) {
                return;
            }

            if ((PatcherConfig.dontCullNametags && entity instanceof EntityPlayer) ||
                (PatcherConfig.dontCullEntityNametags && !(entity instanceof EntityArmorStand)) ||
                (PatcherConfig.dontCullArmourStandNametags && entity instanceof EntityArmorStand)) {
                event.renderer.renderName(entity, event.x, event.y, event.z);
            }
        }

    }


    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        final WorldClient theWorld = mc.theWorld;
        if (theWorld == null) {
            return;
        }

        List<UUID> remove = new ArrayList<>();

        outer:
        for (OcclusionQuery value : queries.values()) {
            for (Entity entity : theWorld.loadedEntityList) {
                if (entity.getUniqueID() == value.uuid) {
                    continue outer;
                }
            }

            remove.add(value.uuid);
            if (value.nextQuery != 0) {
                GL15.glDeleteQueries(value.nextQuery);
            }
        }

        for (UUID uuid : remove) {
            queries.remove(uuid);
        }

        for (OcclusionQuery query : queries.values()) {
            if (query.nextQuery != 0) {
                final long queryObject = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT_AVAILABLE);
                if (queryObject != 0) {
                    query.occluded = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT) == 0;
                    GL15.glDeleteQueries(query.nextQuery);
                    query.nextQuery = 0;
                    query.refresh = true;
                }
            }
        }
    }

    static class OcclusionQuery {
        private final UUID uuid; //Owner
        private int nextQuery;
        private boolean refresh = true;
        private boolean occluded;

        public OcclusionQuery(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
