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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used for stopping entities from rendering if they are not visible to the player
 * Subsequent entity on entity occlusion derived from https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
 */
public class EntityCulling {

    private static final ExecutorService service = Executors.newFixedThreadPool(8, task -> new Thread(task, "Culling Thread"));
    private static final Set<Entity> exclude = Sets.newConcurrentHashSet(); //Set of entities to not exclude from rendering
    private static final Minecraft mc = Minecraft.getMinecraft(); //Minecraft instance
    private static final ReentrantLock lock = new ReentrantLock(); //Lock to manage syncing of async entity on entity checking
    public static boolean uiRendering; //Set via asm to determine whether the UI is rendering. If the UI is rendering, we should still render the entity to avoid interfering with mods that render on screen entities
    private static CountDownLatch latch = null; //Used to keep track of progress of async culling


    /**
     * Begins the algorithm for checking whether entities are visible.
     */
    public static void begin() {
        exclude.clear();

        if (!PatcherConfig.entityCulling) {
            return;
        }

        //Don't begin if not in a world
        final World world = mc.theWorld;
        if (world == null || mc.thePlayer == null) {
            return;
        }

        //Use a CountdownLatch of size for all entities in the world to keep track of concurrent progress
        latch = new CountDownLatch(world.loadedEntityList.size());

        final Set<Entity> render = Sets.newConcurrentHashSet(); //Use concurrent set as many threads will be adding items async
        Iterator<Entity> entityIterator = world.loadedEntityList.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (entityIterator.hasNext()) {
            final Entity entity = entityIterator.next();
            if (entity == mc.thePlayer) { //The player should always be visible to itself
                latch.countDown();
                continue;
            }
            //Execute asynchronously in pool for faster total runtime on machines with multiple cores
            service.submit(() -> doBlockVisibilityCheck(entity, world, render));
        }

        /*if (PatcherConfig.entitySightCulling) //Option to not perform entity on entity calculations
            service.submit(() -> doEntityOnEntityVisibilityCheck(render));*/
    }


    /**
     * Determines whether the corners of this entity's bounding box and 4 points down the middle
     * of the box are all hidden behind blocks. If all 12 rays fail to reach the target, it is
     * added to the exclusion list. Otherwise it is added to the render list for use in the entity
     * on entity visibility checks
     *
     * @param entity Entity to check
     * @param world  World to check in
     * @param render Set of entities to render. If any rays reach target, Entity will be added to this list
     */
    private static void doBlockVisibilityCheck(Entity entity, World world, Set<Entity> render) {
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(0, .25, 0);
        double centerX = (box.maxX + box.minX) / 2;
        double centerZ = (box.maxZ + box.minZ) / 2;
        //Origin point where the rays are traced from
        final Vec3 baseVector = getRayOrigin();
        if (
            //8 corners
            doesRayHitBlock(world, baseVector, box.maxX, box.maxY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.maxY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.minY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.minY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.maxY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.maxY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.minY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.minY, box.minZ) ||
                //4 points running down center of bounding box
                doesRayHitBlock(world, baseVector, centerX, box.maxY, centerZ) ||
                doesRayHitBlock(world, baseVector, centerX, box.maxY - ((box.maxY - box.minY) / 4), centerZ) ||
                doesRayHitBlock(world, baseVector, centerX, box.maxY - ((box.maxY - box.minY) * 3 / 4), centerZ) ||
                doesRayHitBlock(world, baseVector, centerX, box.minY, centerZ)
        ) {
            latch.countDown();
            render.add(entity);
            return;
        }

        exclude.add(entity);

        latch.countDown();
    }


    /**
     *
     * @return Point where all rays should be traced from for visibility checks
     */
    private static Vec3 getRayOrigin() {
        return mc.thePlayer.getPositionVector().addVector(0, mc.thePlayer.getEyeHeight(), 0);
    }


    /**
     * Performs entity on entity
     * @param render Set of entities to check for visibility on
     */
    private static void doEntityOnEntityVisibilityCheck(Set<Entity> render) {
        lock.lock();
        try {
            latch.await(); //Wait for block checks to complete first
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Nothing to do
        if(render.size() == 0) {
            lock.unlock();
            return;
        }
        //Sort entities by distance from player. Assumption made that closer entities will take up a higher arc on the screen
        PriorityQueue<Entity> entities = new PriorityQueue<>(render.size(), Comparator.comparingDouble(value -> mc.thePlayer.getDistanceSqToEntity(value)));
        entities.addAll(render);

        List<AxisAlignedBB> list = new ArrayList<>(); //List of entities that are visible

        //Use this over Minecraft's Vec3 in order to avoid the creation of tons of objects that saturate eden space
        final TripleVector center = new TripleVector();
        final TripleVector bottom = new TripleVector();
        final TripleVector direction = new TripleVector();
        for (Entity entity : entities) {
            if (entity == mc.thePlayer) continue; //Don't check the player

            if(entity.isInvisible()) { //Don't run this check on invisible entities
                continue;
            }
            final AxisAlignedBB box = entity.getEntityBoundingBox();

            //First entity checked should be visible
            if (list.isEmpty()) {
                list.add(box);
                continue;
            }

            double centerX = (box.maxX + box.minX) / 2;
            double centerZ = (box.maxZ + box.minZ) / 2;

            final Vec3 playerPosition = getRayOrigin();
            direction.reset((box.maxX + box.minX) / 2 - playerPosition.xCoord,
                (box.maxY + box.minY) / 2 - playerPosition.yCoord,
                (box.maxZ + box.minZ) / 2 - playerPosition.zCoord);
            direction.normalize();

            final ArrayList<AxisAlignedBB> scan = new ArrayList<>(); //List of bounding boxes in the right direction

            for (AxisAlignedBB otherPotentialBox : list) {
                final double xCoord = (otherPotentialBox.maxX + otherPotentialBox.minX) / 2 - playerPosition.xCoord;
                final double zCoord = (otherPotentialBox.maxZ + otherPotentialBox.minZ) / 2 - playerPosition.zCoord;

                center.reset(xCoord, (otherPotentialBox.maxY + otherPotentialBox.minY) / 2 - playerPosition.yCoord, zCoord);
                center.normalize();

                bottom.reset(xCoord, (otherPotentialBox.maxY) - playerPosition.yCoord, zCoord);
                bottom.normalize();


                //Determine if this box is in the right direction to perform ray traces on
                if (center.dotProduct(direction) >= bottom.dotProduct(center)) {
                    scan.add(otherPotentialBox);
                }
            }

            if (scan.size() == 0 ||
                //8 corners
                doesRayHitEntity(playerPosition, box.maxX, box.maxY, box.maxZ, scan) ||
                doesRayHitEntity(playerPosition, box.maxX, box.maxY, box.minZ, scan) ||
                doesRayHitEntity(playerPosition, box.maxX, box.minY, box.maxZ, scan) ||
                doesRayHitEntity(playerPosition, box.maxX, box.minY, box.minZ, scan) ||
                doesRayHitEntity(playerPosition, box.minX, box.maxY, box.maxZ, scan) ||
                doesRayHitEntity(playerPosition, box.minX, box.maxY, box.minZ, scan) ||
                doesRayHitEntity(playerPosition, box.minX, box.minY, box.maxZ, scan) ||
                doesRayHitEntity(playerPosition, box.minX, box.minY, box.minZ, scan) ||
                //4 points running down center of hitbox
                doesRayHitEntity(playerPosition, centerX, box.maxY, centerZ, scan) ||
                doesRayHitEntity(playerPosition, centerX, box.maxY - ((box.maxY - box.minY) / 4), centerZ, scan) ||
                doesRayHitEntity(playerPosition, centerX, box.maxY - ((box.maxY - box.minY) * 3 / 4), centerZ, scan) ||
                doesRayHitEntity(playerPosition, centerX, box.minY, centerZ, scan)
            ) {
                if (!(entity instanceof EntityArmorStand)) //These are generally visible
                    list.add(box);
                continue;
            }
            exclude.add(entity);
        }
        lock.unlock();
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
    private static boolean doesRayHitBlock(World worldObj, Vec3 base, double x, double y, double z) {
        return rayTraceBlocks(worldObj, new TripleVector(base), new TripleVector(x, y, z)) == null;
    }

    /**
     * @return true of it intercepted the hitbox
     */
    private static boolean interceptsInRange(Vec3 la, Vec3 lb, Vec3 v1, Vec3 v2, Vec3 planePoint) {

        //A maximum of three faces of a rectangular prism can be visible at once
        //Determine if this face is visible and if its not, save calculations by not processing it
        Vec3 cross = v1.crossProduct(v2);
        final double v3 = cross.dotProduct(la.subtract(lb));
        if (v3 > 0) {
            return false;
        }

        //Determine the intersection of the ray and the plane
        final Vec3 ab = lb.subtract(la);
        final Vec3 minusAB = new Vec3(-ab.xCoord, -ab.yCoord, -ab.zCoord);
        final Vec3 subtract = la.subtract(planePoint);
        final double v4 = minusAB.dotProduct(v1.crossProduct(v2));
        double u = v2.crossProduct(minusAB).dotProduct(subtract) / v4;
        double v = minusAB.crossProduct(v1).dotProduct(subtract) / v4;

        //If both u nad v are between [0,1] then the line intersections the plane within the area rectangle
        //In this case, it is within the bounds of the bounding box
        return (u >= 0 && u <= 1 && v >= 0 && v <= 1);
    }

    /**
     * Does the ray fired from the players eyes land on an entity
     *
     * @param la Our player.
     * @param x  Entity bounding box X.
     * @param y  Entity bounding box Y.
     * @param z  Entity bounding box Z.
     * @return The status on if the raytrace hits the entity.
     */
    private static boolean doesRayHitEntity(Vec3 la, double x, double y, double z, List<AxisAlignedBB> boxes) {
        final Vec3 lb = new Vec3(x, y, z); //Maybe not need to normalize?

        //Parametric form of line is la + lb * u
        final TripleVector p1 = new TripleVector();
        final TripleVector p2 = new TripleVector();
        final TripleVector v1 = new TripleVector();
        final TripleVector v2 = new TripleVector();
        final TripleVector v3 = new TripleVector();

        for (AxisAlignedBB box : boxes) {
            //First plane
            p1.reset(box.minX, box.minY, box.minZ);
            p2.reset(box.maxX, box.maxY, box.maxZ);

            v1.reset(box.maxX - box.minX, 0, 0);
            v2.reset(0, box.maxY - box.minY, 0);
            v3.reset(0, 0, box.maxZ - box.minZ);
            v3.reset(0, 0, box.maxZ - box.minZ);

            if (checkFaces(la, lb, p1, v3, v2, v1)) return false;
            invert(v1);
            invert(v2);
            invert(v3);
            if (checkFaces(la, lb, p2, v1, v2, v3)) return false;
        }
        return true;
    }

    /**
     * Checks for interception on faces
     */
    private static boolean checkFaces(Vec3 la, Vec3 lb, TripleVector p2, TripleVector v1, TripleVector v2, TripleVector v3) {
        return interceptsInRange(la, lb, v3.toMinecraftVector(), v2.toMinecraftVector(), p2.toMinecraftVector()) ||
            interceptsInRange(la, lb, v2.toMinecraftVector(), v1.toMinecraftVector(), p2.toMinecraftVector()) ||
            interceptsInRange(la, lb, v3.toMinecraftVector(), v1.toMinecraftVector(), p2.toMinecraftVector());
    }

    private static void invert(TripleVector src) {
        src.xCoord = -src.xCoord;
        src.yCoord = -src.yCoord;
        src.zCoord = -src.zCoord;
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
        if (mc.gameSettings.thirdPersonView != 0 || uiRendering) return; // TODO: 9/2/2020 3rd person culling

        EntityLivingBase entity = event.entity;
        if (exclude.contains(entity) && !entity.isEntityInsideOpaqueBlock()) {
            event.setCanceled(true);
            if (PatcherConfig.dontCullNametags && canRenderName(entity) && event.isCanceled()) {
                event.renderer.renderName(entity, event.x, event.y, event.z);
            }
        }

    }
    /*]     * Used for checking if the entities nametag can be rendered if the user still wants
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

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if ((/*!PatcherConfig.entitySightCulling &&*/ !PatcherConfig.entityCulling) || event.phase != TickEvent.Phase.END || latch == null) {
            return;
        }

        try {
            latch.await();
            lock.lock();
            lock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static final class TripleVector {
        private double xCoord, yCoord, zCoord;

        public TripleVector() {
            this(0, 0, 0);
        }

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

        public double dotProduct(TripleVector vec) {
            return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
        }

        public void normalize() {
            double d0 = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
            if (d0 < 1.0E-4D) {
                reset(0, 0, 0);
            }
            reset(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
        }
    }
}
