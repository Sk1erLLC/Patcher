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
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Used for stopping entities from rendering if they are not visible to the player.
 */
public class EntityCulling {

    private static final Set<Entity> exclude = Sets.newConcurrentHashSet();
    private static final ExecutorService service = Executors.newFixedThreadPool(8, r -> new Thread(r, "Cull Thread"));
    private static CountDownLatch latch = null;

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

    public static void begin() {
        if (!PatcherConfig.entityCulling) return;
        exclude.clear();
        World theWorld = Minecraft.getMinecraft().theWorld;
        final Entity thePlayer = Minecraft.getMinecraft().thePlayer;
        if (theWorld == null) return;
        int amt = 0;
        for (Entity entity : theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && entity != thePlayer) amt++;
        }
        latch = new CountDownLatch(amt);
        for (Entity entity : theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || entity == thePlayer) continue;
            service.submit(() -> {
                //like top front left, top bottom right, bottom back left, top back right -> maxY maxX minZ, maxY minX maxZ, minY minX minZ,minY minX maxZ
                if (thePlayer != null && !entity.isEntityInsideOpaqueBlock() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
                    AxisAlignedBB box = entity.getEntityBoundingBox();
                    long l = System.nanoTime();
                    double centerX = (box.maxX + box.minX) / 2;
                    double centerZ = (box.maxZ + box.minZ) / 2;
                    final net.minecraft.util.Vec3 base = thePlayer.getPositionVector().addVector(0, thePlayer.getEyeHeight(), 0);
                    if (
                        //8 corners
                        doesRayHitEntity(theWorld, base, box.maxX, box.maxY, box.maxZ) ||
                            doesRayHitEntity(theWorld, base, box.maxX, box.maxY, box.minZ) ||
                            doesRayHitEntity(theWorld, base, box.maxX, box.minY, box.maxZ) ||
                            doesRayHitEntity(theWorld, base, box.maxX, box.minY, box.minZ) ||
                            doesRayHitEntity(theWorld, base, box.minX, box.maxY, box.maxZ) ||
                            doesRayHitEntity(theWorld, base, box.minX, box.maxY, box.minZ) ||
                            doesRayHitEntity(theWorld, base, box.minX, box.minY, box.maxZ) ||
                            doesRayHitEntity(theWorld, base, box.minX, box.minY, box.minZ)
                            ||
//
//                                //4 points running down center of hitbox
                            doesRayHitEntity(theWorld, base, centerX, box.maxY, centerZ) ||
                            doesRayHitEntity(theWorld, base, centerX, box.maxY - ((box.maxY - box.minY) / 4), centerZ) ||
                            doesRayHitEntity(theWorld, base, centerX, box.maxY - ((box.maxY - box.minY) * 3 / 4), centerZ) ||
                            doesRayHitEntity(theWorld, base, centerX, box.minY, centerZ)
                    ) {
                        latch.countDown();
                        return;
                    }
                    exclude.add(entity);
                }
                latch.countDown();

            });
        }
    }

    /**
     * Does the ray fired from the players eyes land on an entity?
     *
     * @param worldObj Current world.
     * @param base   Our player.
     * @param x        Entity bounding box X.
     * @param y        Entity bounding box Y.
     * @param z        Entity bounding box Z.
     * @return The status on if the raytrace hits the entity.
     */
    private static boolean doesRayHitEntity(World worldObj, net.minecraft.util.Vec3 base, double x, double y, double z) {
        return rayTraceBlocks(worldObj,
//            new Vec3(player.getPositionVector().addVector(0, player.getEyeHeight(), 0)),
            new Vec3(base),
            new Vec3(x, y, z)
        ) == null;
    }

    /**
     * Fire a ray hitting blocks, used for checking if an entity is behind a block.
     *
     * @param theWorld Current world.
     * @param from     From the player.
     * @param to       To the block.
     * @return The ray trace result.
     */
    private static MovingObjectPosition rayTraceBlocks(World theWorld, Vec3 from, Vec3 to) {

        if (!Double.isNaN(from.xCoord) && !Double.isNaN(from.yCoord) && !Double.isNaN(from.zCoord)) {
            if (!Double.isNaN(to.xCoord) && !Double.isNaN(to.yCoord) && !Double.isNaN(to.zCoord)) {
                int i = MathHelper.floor_double(to.xCoord);
                int j = MathHelper.floor_double(to.yCoord);
                int k = MathHelper.floor_double(to.zCoord);
                int l = MathHelper.floor_double(from.xCoord);
                int i1 = MathHelper.floor_double(from.yCoord);
                int j1 = MathHelper.floor_double(from.zCoord);
                BetterBlockPos blockpos = new BetterBlockPos(l, i1, j1);
                IBlockState iblockstate = theWorld.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (!(block instanceof BlockAir)) {
                    if (!block.isOpaqueCube() || !block.isFullCube()) {
                        return null;
                    }
                }

                if (block.canCollideCheck(iblockstate, false)) {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(theWorld, blockpos, from.toMc(), to.toMc());
                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return null;
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
                    double d6 = to.xCoord - from.xCoord;
                    double d7 = to.yCoord - from.yCoord;
                    double d8 = to.zCoord - from.zCoord;

                    if (flag2) {
                        d3 = (d0 - from.xCoord) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - from.yCoord) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - from.zCoord) / d8;
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
                        from.reset(d0, from.yCoord + d7 * d3, from.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        from.reset(from.xCoord + d6 * d4, d1, from.zCoord + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        from.reset(from.xCoord + d6 * d5, from.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(from.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(from.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(from.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos.update(l, i1, j1);
                    IBlockState iblockstate1 = theWorld.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!(block1 instanceof BlockAir)) {
                        if (!block1.isOpaqueCube() || !block1.isFullCube()) {
                            return null;
                        }
                    }

                    if (block1.canCollideCheck(iblockstate1, false)) {
                        MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(theWorld, blockpos, from.toMc(), to.toMc());
                        if (movingobjectposition1 != null) {
                            return movingobjectposition1;
                        }
                    }
                }

            }
        }
        return null;
    }

    /**
     * Fire rays from the player's eyes, detecting on if it can see an entity or not.
     * If it can see an entity, continue to render the entity, otherwise save some time
     * performing rendering and cancel the entity render.
     *
     * @param event {@link RenderLivingEvent.Pre}
     */
    @SubscribeEvent
    public void shouldRenderEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!PatcherConfig.entityCulling) return;

        if (exclude.contains(event.entity)) {
            event.setCanceled(true);
            if (PatcherConfig.dontCullNametags && canRenderName(event.entity) && event.isCanceled()) {
                event.renderer.renderName(event.entity, event.x, event.y, event.z);
            }
        }

    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (!PatcherConfig.entityCulling) return;
        if (event.phase != TickEvent.Phase.END || latch == null) return;
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class BetterBlockPos extends BlockPos {

        private int x, y, z;

        public BetterBlockPos(int x, int y, int z) {
            super(x, y, z);
            this.x = x;
            this.z = z;
            this.y = y;
        }

        private void update(int x, int y, int z) {
            this.x = x;
            this.z = z;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }
    }

    static final class Vec3 {
        private double xCoord, yCoord, zCoord;

        public Vec3(double xCoord, double yCoord, double zCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }

        public Vec3(net.minecraft.util.Vec3 addVector) {
            this(addVector.xCoord, addVector.yCoord, addVector.zCoord);
        }

        private net.minecraft.util.Vec3 toMc() {
            return new net.minecraft.util.Vec3(xCoord, yCoord, zCoord);
        }

        private void reset(double xCoord, double yCoord, double zCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }
    }
}
