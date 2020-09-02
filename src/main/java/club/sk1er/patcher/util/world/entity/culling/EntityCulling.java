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

import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Sets;
import kotlin.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used for stopping entities from rendering if they are not visible to the player.
 */
public class EntityCulling {

    private static final ExecutorService service = Executors.newFixedThreadPool(8, task -> new Thread(task, "Culling Thread"));
    private static final Set<Entity> exclude = Sets.newConcurrentHashSet();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final List<Pair<TripleVector, TripleVector>> hits = Collections.synchronizedList(new ArrayList<>());
    private static final List<Pair<TripleVector, TripleVector>> misses = Collections.synchronizedList(new ArrayList<>());
    public static boolean uiRendering;
    private static CountDownLatch latch = null;
    int culled;
    int not;

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

    private static void doBlockVisibityCheck(Entity entity, World world, Set<Entity> render) {
        //like top front left, top bottom right, bottom back left, top back right -> maxY maxX minZ, maxY minX maxZ, minY minX minZ,minY minX maxZ
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(0, .25, 0);
        double centerX = (box.maxX + box.minX) / 2;
        double centerZ = (box.maxZ + box.minZ) / 2;
        final Vec3 baseVector = mc.thePlayer.getPositionVector().addVector(0, mc.thePlayer.getEyeHeight(), 0);
        if (true ||
            //8 corners
            doesRayHitBlock(world, baseVector, box.maxX, box.maxY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.maxY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.minY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.maxX, box.minY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.maxY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.maxY, box.minZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.minY, box.maxZ) ||
                doesRayHitBlock(world, baseVector, box.minX, box.minY, box.minZ) ||
                //4 points running down center of hitbox
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

    public static void begin() {
        if (!PatcherConfig.entityCulling || uiRendering) {
            return;
        }

        exclude.clear();

        final World world = mc.theWorld;

        if (world == null || mc.thePlayer == null) {
            return;
        }

        latch = new CountDownLatch(world.loadedEntityList.size());

        final Set<Entity> render = Sets.newConcurrentHashSet();
        Iterator<Entity> entityIterator = world.loadedEntityList.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (entityIterator.hasNext()) {
            final Entity entity = entityIterator.next();
            if (!(entity instanceof EntityLivingBase) || entity == mc.thePlayer) {
                latch.countDown();
                continue;
            }
            service.submit(() -> doBlockVisibityCheck(entity, world, render));
        }
        Multithreading.submit(() -> doEntityRayTrace(render));
    }

    private static void doEntityRayTrace(Set<Entity> render) {
        lock.lock();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PriorityQueue<Entity> entities = new PriorityQueue<>(Math.max(1, render.size()), Comparator.comparingDouble(value -> mc.thePlayer.getDistanceSqToEntity(value)));
        entities.addAll(render);
        int addl = 0;
        List<AxisAlignedBB> list = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity == mc.thePlayer) continue;
            final AxisAlignedBB box = entity.getEntityBoundingBox();
            if (list.isEmpty()) {
                list.add(box);
                continue;
            }

            double centerX = (box.maxX + box.minX) / 2;
            double centerZ = (box.maxZ + box.minZ) / 2;
            final Vec3 playerPosition = mc.thePlayer.getPositionVector().addVector(0, mc.thePlayer.getEyeHeight(), 0);
            final Vec3 direction = new Vec3((box.maxX + box.minX) / 2 - playerPosition.xCoord,
                (box.maxY + box.minY) / 2 - playerPosition.yCoord,
                (box.maxZ + box.minZ) / 2 - playerPosition.zCoord).normalize();
            final ArrayList<AxisAlignedBB> dest = new ArrayList<>();
            for (AxisAlignedBB otherPotentialBox : list) {
                Vec3 tmp = new Vec3((otherPotentialBox.maxX + otherPotentialBox.minX) / 2 - playerPosition.xCoord,
                    (otherPotentialBox.maxY + otherPotentialBox.minY) / 2 - playerPosition.yCoord,
                    (otherPotentialBox.maxZ + otherPotentialBox.minZ) / 2 - playerPosition.zCoord).normalize();

                Vec3 bottom = new Vec3(otherPotentialBox.maxX - playerPosition.xCoord,
                    otherPotentialBox.maxY - playerPosition.yCoord,
                    otherPotentialBox.maxZ - playerPosition.zCoord).normalize();
                final double v = tmp.dotProduct(direction);
//                if (v >= bottom.dotProduct(direction)) {
                    dest.add(otherPotentialBox);
//                }
            }
            if (dest.size() == 0 || entity.getCustomNameTag().contains("Test")||
                //8 corners
                doesRayHitEntity(playerPosition, box.maxX, box.maxY, box.maxZ, dest)
                ||
                doesRayHitEntity(playerPosition, box.maxX, box.maxY, box.minZ, dest) ||
                doesRayHitEntity(playerPosition, box.maxX, box.minY, box.maxZ, dest) ||
                doesRayHitEntity(playerPosition, box.maxX, box.minY, box.minZ, dest) ||
                doesRayHitEntity(playerPosition, box.minX, box.maxY, box.maxZ, dest) ||
                doesRayHitEntity(playerPosition, box.minX, box.maxY, box.minZ, dest) ||
                doesRayHitEntity(playerPosition, box.minX, box.minY, box.maxZ, dest) ||
                doesRayHitEntity(playerPosition, box.minX, box.minY, box.minZ, dest) ||
                //4 points running down center of hitbox
                doesRayHitEntity(playerPosition, centerX, box.maxY, centerZ, dest) ||
                doesRayHitEntity(playerPosition, centerX, box.maxY - ((box.maxY - box.minY) / 4), centerZ, dest) ||
                doesRayHitEntity(playerPosition, centerX, box.maxY - ((box.maxY - box.minY) * 3 / 4), centerZ, dest) ||
                doesRayHitEntity(playerPosition, centerX, box.minY, centerZ, dest)
            ) {
                if (!(entity instanceof EntityArmorStand))
                    list.add(box);
                continue;
            }
//                System.out.println("exclude: " + entity.getName());
            exclude.add(entity);
            addl++;
        }
//            System.out.println("ADDL: " + addl);
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

    /***
     *
     * @param la
     * @param lb
     * @param v1
     * @param v2
     * @param planePoint
     * @return true of it intercepted the hitbox
     */
    private static boolean interceptsInRange(Vec3 la, Vec3 lb, Vec3 v1, Vec3 v2, Vec3 planePoint) {
        //Is not facing the right direction
         Vec3 cross = v1.crossProduct(v2);
        final double v3 = cross.dotProduct(la.subtract(lb));
//        System.out.println(v3);
        if (v3 > 0) {
            return false;
        }

        //Find the point where the plane intercepts (dot product > 0 means it has to intersect somewhere)

        final Vec3 ab = lb.subtract(la);
        final Vec3 minusAB = new Vec3(-ab.xCoord, -ab.yCoord, -ab.zCoord);
        double u = v2.crossProduct(minusAB).dotProduct(la.subtract(planePoint)) / (minusAB.dotProduct(v1.crossProduct(v2)));
        double v = minusAB.crossProduct(v1).dotProduct(la.subtract(planePoint)) / (minusAB.dotProduct(v1.crossProduct(v2)));
//        System.out.println("U: " + u + " V: " + v);

        return (u >= 0 && u <= 1 && v >= 0 && v <= 1);
    }

    /**
     * Does the ray fired from the players eyes land on an entity?
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
        for (AxisAlignedBB box : boxes) {
            //First plane
            double[] data = new double[]{box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ};
            Vec3 p1 = new Vec3(data[0], data[1], data[2]);
            Vec3 p2 = new Vec3(data[3], data[4], data[5]);

            Vec3 v1 = new Vec3(data[3] - data[0], 0, 0);
            Vec3 v2 = new Vec3(0, data[4] - data[1], 0);
            Vec3 v3 = new Vec3(0, 0, data[5] - data[2]);

            //Find out which planes are visible: Take cross
            if (
                //S1, S2, S3
                interceptsInRange(la, lb, v1, v2, p1)
                    ||
                    interceptsInRange(la, lb, v2, v3, p1)
                        ||
                    interceptsInRange(la, lb, v1, v3, p1)
                        ||
//                    //S4, S5, S6
                    interceptsInRange(la, lb, invert(v3), invert(v2), p2)
                        ||
                    interceptsInRange(la, lb, invert(v2), invert(v1), p2)
                    ||
                    interceptsInRange(la, lb, invert(v3), invert(v1), p2)
            ) {
                misses.add(new Pair<>(new TripleVector(la), new TripleVector(x, y, z)));

                return false;
            }
        }
        hits.add(new Pair<>(new TripleVector(la), new TripleVector(x, y, z)));
        return true;
    }
    private static Vec3 invert(Vec3 src) {
        return new Vec3(-src.xCoord,-src.yCoord,-src.zCoord);
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
     * Fire a ray hitting entities, used for checking if an entity is behind another entity.
     *
     * @param fromPoint From the player.
     * @param toPoint   To the block.
     * @param boxes     list of boxes to check for inclusion within
     * @return The ray trace result.
     */
    private static boolean rayTraceEntity(TripleVector fromPoint, TripleVector toPoint, List<AxisAlignedBB> boxes) {
        if (!Double.isNaN(fromPoint.xCoord) && !Double.isNaN(fromPoint.yCoord) && !Double.isNaN(fromPoint.zCoord)) {
            if (!Double.isNaN(toPoint.xCoord) && !Double.isNaN(toPoint.yCoord) && !Double.isNaN(toPoint.zCoord)) {
                int fromX = MathHelper.floor_double(fromPoint.xCoord);
                int fromY = MathHelper.floor_double(fromPoint.yCoord);
                int fromZ = MathHelper.floor_double(fromPoint.zCoord);
                int toX = MathHelper.floor_double(toPoint.xCoord);
                int toY = MathHelper.floor_double(toPoint.yCoord);
                int toZ = MathHelper.floor_double(toPoint.zCoord);

                int counter = 200;
                while (counter-- >= 0) {
                    if (Double.isNaN(fromPoint.xCoord) || Double.isNaN(fromPoint.yCoord) || Double.isNaN(fromPoint.zCoord)) {
                        return false;
                    }

                    if (fromX == toX && fromY == toY && fromZ == toZ) {
                        return false;
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
                    for (AxisAlignedBB axisAlignedBB : boxes) {
                        if (axisAlignedBB.isVecInside(new Vec3(fromPoint.xCoord, fromPoint.yCoord, fromPoint.zCoord))) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if ((hits.size() > 0 || misses.size() > 0)) {

            lock.lock();
            lock.unlock();
            GlStateManager.pushMatrix();
            final EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
            GlStateManager.translate(-thePlayer.posX, -thePlayer.posY, -thePlayer.posZ);
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            for (Pair<TripleVector, TripleVector> hit : hits) {
                if (hit == null || hit.component1() == null || hit.component2() == null) continue;
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(hit.component1().xCoord, hit.component1().yCoord, hit.component1().zCoord).color(0, 255, 0, 255).endVertex();
                worldrenderer.pos(hit.component2().xCoord, hit.component2().yCoord, hit.component2().zCoord).color(0, 255, 0, 255).endVertex();
                tessellator.draw();

            }
            for (Pair<TripleVector, TripleVector> hit : misses) {
                if (hit == null || hit.component1() == null || hit.component2() == null) continue;

                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(hit.component1().xCoord, hit.component1().yCoord, hit.component1().zCoord).color(255, 0, 0, 255).endVertex();
                worldrenderer.pos(hit.component2().xCoord, hit.component2().yCoord, hit.component2().zCoord).color(255, 0, 0, 255).endVertex();
                tessellator.draw();
            }
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();

//        for (Pair<TripleVector, TripleVector> hit : misses) {
//            GL11.glVertex3d(hit.component1().xCoord, hit.component1().yCoord, hit.component1().zCoord);
//            GL11.glVertex3d(hit.component2().xCoord, hit.component2().yCoord, hit.component2().zCoord);
//        }

        }
    }

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

//        System.out.println(culled + ", " + not);
        culled = 0;
        not = 0;
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
//        event.setCanceled(true);
//        if (!PatcherConfig.entityCulling || mc.gameSettings.thirdPersonView != 0) return;

        EntityLivingBase entity = event.entity;
        if (exclude.contains(entity)) {
            culled++;
        }
        if (exclude.contains(entity) && !entity.isEntityInsideOpaqueBlock()) {
            event.setCanceled(true);
            if (PatcherConfig.dontCullNametags && canRenderName(entity) && event.isCanceled()) {
                event.renderer.renderName(entity, event.x, event.y, event.z);
            }
        } else not++;

    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (!PatcherConfig.entityCulling || event.phase != TickEvent.Phase.END || latch == null) {
            return;
        }
        hits.clear();
        misses.clear();
        long start = System.currentTimeMillis();
        try {
            latch.await();
            lock.lock();
            lock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Wait time: " + (System.currentTimeMillis() - start));
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
