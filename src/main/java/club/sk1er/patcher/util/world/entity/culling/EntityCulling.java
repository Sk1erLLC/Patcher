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

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Used for stopping entities from rendering if they are not visible to the player.
 */
public class EntityCulling {

    private static final ExecutorService service = Executors.newFixedThreadPool(8, task -> new Thread(task, "Culling Thread"));
    private static final Set<Entity> exclude = Sets.newConcurrentHashSet();
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean uiRendering;
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

    public static void begin() {
        if (!PatcherConfig.entityCulling || uiRendering) {
            return;
        }

        exclude.clear();

        World world = mc.theWorld;

        if (world == null || mc.thePlayer == null) {
            return;
        }

        latch = new CountDownLatch(world.loadedEntityList.size());

        Iterator<Entity> entityIterator = world.loadedEntityList.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (!(entity instanceof EntityLivingBase) || entity == mc.thePlayer) {
                latch.countDown();
                continue;
            }

            service.submit(() -> {
                //like top front left, top bottom right, bottom back left, top back right -> maxY maxX minZ, maxY minX maxZ, minY minX minZ,minY minX maxZ
                AxisAlignedBB box = entity.getEntityBoundingBox();
                double centerX = (box.maxX + box.minX) / 2;
                double centerZ = (box.maxZ + box.minZ) / 2;
                final Vec3 baseVector = mc.thePlayer.getPositionVector().addVector(0, mc.thePlayer.getEyeHeight(), 0);
                if (
                    //8 corners
                    doesRayHitEntity(world, baseVector, box.maxX, box.maxY, box.maxZ) ||
                        doesRayHitEntity(world, baseVector, box.maxX, box.maxY, box.minZ) ||
                        doesRayHitEntity(world, baseVector, box.maxX, box.minY, box.maxZ) ||
                        doesRayHitEntity(world, baseVector, box.maxX, box.minY, box.minZ) ||
                        doesRayHitEntity(world, baseVector, box.minX, box.maxY, box.maxZ) ||
                        doesRayHitEntity(world, baseVector, box.minX, box.maxY, box.minZ) ||
                        doesRayHitEntity(world, baseVector, box.minX, box.minY, box.maxZ) ||
                        doesRayHitEntity(world, baseVector, box.minX, box.minY, box.minZ) ||
                        //4 points running down center of hitbox
                        doesRayHitEntity(world, baseVector, centerX, box.maxY, centerZ) ||
                        doesRayHitEntity(world, baseVector, centerX, box.maxY - ((box.maxY - box.minY) / 4), centerZ) ||
                        doesRayHitEntity(world, baseVector, centerX, box.maxY - ((box.maxY - box.minY) * 3 / 4), centerZ) ||
                        doesRayHitEntity(world, baseVector, centerX, box.minY, centerZ)
                ) {
                    latch.countDown();
                    return;
                }

                exclude.add(entity);

                latch.countDown();
            });
        }
        entityIterator = null;
    }

    /**
     * Does the ray fired from the players eyes land on an entity?
     *
     * @param worldObj Current world.
     * @param base     Our player.
     * @param x        Entity bounding box X.
     * @param y        Entity bounding box Y.
     * @param z        Entity bounding box Z.
     * @return The status on if the raytrace hits the entity.
     */
    private static boolean doesRayHitEntity(World worldObj, Vec3 base, double x, double y, double z) {
        return rayTraceBlocks(worldObj, new TripleVector(base), new TripleVector(x, y, z)) == null;
    }

    /**
     * Fire a ray hitting blocks, used for checking if an entity is behind a block.
     *
     * @param world     Current world.
     * @param fromPoint From the player.
     * @param toPoint   To the block.
     * @return The ray trace result.
     */
    private static MovingObjectPosition rayTraceBlocks(World world, TripleVector fromPoint, TripleVector toPoint) {
        if (!Double.isNaN(fromPoint.xCoord) && !Double.isNaN(fromPoint.yCoord) && !Double.isNaN(fromPoint.zCoord)) {
            if (!Double.isNaN(toPoint.xCoord) && !Double.isNaN(toPoint.yCoord) && !Double.isNaN(toPoint.zCoord)) {
                int fromX = MathHelper.floor_double(fromPoint.xCoord);
                int fromY = MathHelper.floor_double(fromPoint.yCoord);
                int fromZ = MathHelper.floor_double(fromPoint.zCoord);
                int toX = MathHelper.floor_double(toPoint.xCoord);
                int toY = MathHelper.floor_double(toPoint.yCoord);
                int toZ = MathHelper.floor_double(toPoint.zCoord);
                BetterBlockPos blockPosition = new BetterBlockPos(fromX, fromY, fromZ);

                int counter = 200;
                while (counter-- >= 0) {
                    if (Double.isNaN(fromPoint.xCoord) || Double.isNaN(fromPoint.yCoord) || Double.isNaN(fromPoint.zCoord)) {
                        return null;
                    }

                    if (fromX == toX && fromY == toY && fromZ == toZ) {
                        return null;
                    }

                    boolean xBoundaryMet = true;
                    boolean yBoundaryMet = true;
                    boolean zBoundaryMet = true;
                    double distX = 999.0D;
                    double distY = 999.0D;
                    double distZ = 999.0D;

                    if (toX > fromX) {
                        distX = (double) fromX + 1.0D;
                    } else if (toX < fromX) {
                        distX = (double) fromX + 0.0D;
                    } else {
                        xBoundaryMet = false;
                    }

                    if (toY > fromY) {
                        distY = (double) fromY + 1.0D;
                    } else if (toY < fromY) {
                        distY = (double) fromY + 0.0D;
                    } else {
                        yBoundaryMet = false;
                    }

                    if (toZ > fromZ) {
                        distZ = (double) fromZ + 1.0D;
                    } else if (toZ < fromZ) {
                        distZ = (double) fromZ + 0.0D;
                    } else {
                        zBoundaryMet = false;
                    }

                    double finalX = 999.0D;
                    double finalY = 999.0D;
                    double finalZ = 999.0D;
                    double xAvg = toPoint.xCoord - fromPoint.xCoord;
                    double yAvg = toPoint.yCoord - fromPoint.yCoord;
                    double zAvg = toPoint.zCoord - fromPoint.zCoord;

                    if (xBoundaryMet) {
                        finalX = (distX - fromPoint.xCoord) / xAvg;
                    }

                    if (yBoundaryMet) {
                        finalY = (distY - fromPoint.yCoord) / yAvg;
                    }

                    if (zBoundaryMet) {
                        finalZ = (distZ - fromPoint.zCoord) / zAvg;
                    }

                    if (finalX == -0.0D) {
                        finalX = -1.0E-4D;
                    }

                    if (finalY == -0.0D) {
                        finalY = -1.0E-4D;
                    }

                    if (finalZ == -0.0D) {
                        finalZ = -1.0E-4D;
                    }

                    EnumFacing direction;

                    if (finalX < finalY && finalX < finalZ) {
                        direction = toX > fromX ? EnumFacing.WEST : EnumFacing.EAST;
                        fromPoint.reset(distX, fromPoint.yCoord + yAvg * finalX, fromPoint.zCoord + zAvg * finalX);
                    } else if (finalY < finalZ) {
                        direction = toY > fromY ? EnumFacing.DOWN : EnumFacing.UP;
                        fromPoint.reset(fromPoint.xCoord + xAvg * finalY, distY, fromPoint.zCoord + zAvg * finalY);
                    } else {
                        direction = toZ > fromZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        fromPoint.reset(fromPoint.xCoord + xAvg * finalZ, fromPoint.yCoord + yAvg * finalZ, distZ);
                    }

                    fromX = MathHelper.floor_double(fromPoint.xCoord) - (direction == EnumFacing.EAST ? 1 : 0);
                    fromY = MathHelper.floor_double(fromPoint.yCoord) - (direction == EnumFacing.UP ? 1 : 0);
                    fromZ = MathHelper.floor_double(fromPoint.zCoord) - (direction == EnumFacing.SOUTH ? 1 : 0);
                    blockPosition.update(fromX, fromY, fromZ);
                    IBlockState newBlockState = world.getBlockState(blockPosition);
                    Block newBlock = newBlockState.getBlock();

                    if (!(newBlock instanceof BlockAir)) {
                        if (!newBlock.isOpaqueCube() || !newBlock.isFullCube()) {
                            continue;
                        }
                    }

                    if (newBlock.canCollideCheck(newBlockState, false)) {
                        MovingObjectPosition collisionRayTrace = newBlock.collisionRayTrace(world, blockPosition, fromPoint.toMinecraftVector(), toPoint.toMinecraftVector());
                        if (collisionRayTrace != null) {
                            return collisionRayTrace;
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
     * @param event {@link RenderLivingEvent.Pre<EntityLivingBase>}
     */
    @SubscribeEvent
    public void shouldRenderEntity(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!PatcherConfig.entityCulling || mc.gameSettings.thirdPersonView != 0) return;

        EntityLivingBase entity = event.entity;
        if (exclude.contains(entity) && !entity.isEntityInsideOpaqueBlock()) {
            event.setCanceled(true);
            if (PatcherConfig.dontCullNametags && canRenderName(entity) && event.isCanceled()) {
                event.renderer.renderName(entity, event.x, event.y, event.z);
            }
        }

    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (!PatcherConfig.entityCulling || event.phase != TickEvent.Phase.END || latch == null) {
            return;
        }
//        long start = System.nanoTime();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Wait time: " + (System.nanoTime() - start));
    }

    static final class TripleVector {
        private double xCoord, yCoord, zCoord;

        public TripleVector(double xCoord, double yCoord, double zCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }

        public TripleVector(Vec3 addVector) {
            this(addVector.xCoord, addVector.yCoord, addVector.zCoord);
        }

        private Vec3 toMinecraftVector() {
            return new Vec3(xCoord, yCoord, zCoord);
        }

        private void reset(double xCoord, double yCoord, double zCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }
    }
}
