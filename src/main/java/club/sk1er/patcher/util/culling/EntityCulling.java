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

package club.sk1er.patcher.util.culling;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Used for stopping entities from rendering if they are not visible to the player.
 */
public class EntityCulling {

    /**
     * Does the user see the entity that is attempting to render? If they do, continue to render,
     * otherwise don't render the entity.
     *
     * @param event {@link RenderLivingEvent.Pre}
     */
    @SubscribeEvent
    public void shouldRenderEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!PatcherConfig.entityCulling) return;

        World theWorld = event.renderer.getRenderManager().worldObj;
        Entity thePlayer = event.renderer.getRenderManager().livingPlayer;
        Entity objEntity = event.entity;
        //like top front left, top bottom right, bottom back left, top back right -> maxY maxX minZ, maxY minX maxZ, minY minX minZ,minY minX maxZ
        if (theWorld != null && thePlayer != null && objEntity != null && !objEntity.isEntityInsideOpaqueBlock()) {
            AxisAlignedBB box = objEntity.getEntityBoundingBox();
            if (doesRayHitEntity(theWorld, thePlayer, box.maxX, box.maxY, box.minZ)) {
                return;
            }

            if (doesRayHitEntity(theWorld, thePlayer, box.minX, box.maxY, box.maxZ)) {
                return;
            }

            if (doesRayHitEntity(theWorld, thePlayer, box.minX, box.minY, box.minZ)) {
                return;
            }

            if (doesRayHitEntity(theWorld, thePlayer, box.minX, box.minY, box.maxZ)) {
                return;
            }

            event.setCanceled(true);

            if (PatcherConfig.dontCullNametags && canRenderName(event.entity)) {
                event.renderer.renderName(event.entity, event.x, event.y, event.z);
            }
        }
    }

    /**
     * Used for checking if the entities nametag can be rendered if the user still wants
     * to see nametags despite the entity being culled.
     *
     * @param entity The entity that's being culled.
     * @return The status on if the nametag is liable for rendering.
     */
    public static boolean canRenderName(EntityLivingBase entity) {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;

        if (entity instanceof EntityPlayer && entity != entityplayersp) {
            Team team = entity.getTeam();
            Team team1 = entityplayersp.getTeam();

            if (team != null) {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible) {
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null || team.isSameTeam(team1);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null || !team.isSameTeam(team1);
                    case ALWAYS:
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled()
            && entity != Minecraft.getMinecraft().getRenderManager().livingPlayer
            && !entity.isInvisibleToPlayer(entityplayersp)
            && entity.riddenByEntity == null;
    }

    /**
     * Does the ray fired from the players eyes land on an entity?
     *
     * @param worldObj Current world.
     * @param player   Our player.
     * @param x        Entity bounding box X
     * @param y        Entity bounding box Y
     * @param z        Entity bounding box Z
     * @return The status on if the raytrace hits the entity.
     */
    private boolean doesRayHitEntity(World worldObj, Entity player, double x, double y, double z) {
        return rayTraceBlocks(worldObj, player.getPositionVector().addVector(0, player.getEyeHeight(), 0), new Vec3(x, y, z), false, false, false, false) == null;
    }

    /**
     * Fire a ray hitting blocks, used for checking if an entity is behind a block.
     *
     * @param theWorld                      Current world.
     * @param vec31                         From the player
     * @param vec32                         To the block
     * @param stopOnLiquid                  Stop on liquids - always false.
     * @param stopOnTranslucentBlock        Stop on translucent blocks - always false.
     * @param ignoreBlockWithoutBoundingBox Ignore any block without a bounding box - always false,
     * @param returnLastUncollidableBlock   Return the last uncollidable block - always false.
     * @return The ray trace result.
     */
    private MovingObjectPosition rayTraceBlocks(World theWorld, Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean stopOnTranslucentBlock, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord)) {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
                int i = MathHelper.floor_double(vec32.xCoord);
                int j = MathHelper.floor_double(vec32.yCoord);
                int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = theWorld.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (!(block instanceof BlockAir) && !stopOnTranslucentBlock) {
                    if (!block.isOpaqueCube() || !block.isFullCube()) {
                        return null;
                    }
                }

                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(theWorld, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(theWorld, blockpos, vec31, vec32);
                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;

                    if (flag2) {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = theWorld.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!(block1 instanceof BlockAir) && !stopOnTranslucentBlock) {
                        if (!block1.isOpaqueCube() || !block1.isFullCube()) {
                            return null;
                        }
                    }

                    if (!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(theWorld, blockpos, iblockstate1) != null) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(theWorld, blockpos, vec31, vec32);

                            if (movingobjectposition1 != null) {
                                return movingobjectposition1;
                            }
                        } else {
                            movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
