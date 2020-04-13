package club.sk1er.patcher.util.entity;

import club.sk1er.mods.core.ModCore;
import club.sk1er.patcher.keybind.KeybindNameHistory;
import club.sk1er.patcher.screen.ScreenHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.List;

// taken from ScottehBoeh w/ permission (originally from BM Watchdog)
public class EntityTrace {

    private final Minecraft mc = Minecraft.getMinecraft();
    private Entity targetEntity;
    private float partialTicks;

    @SubscribeEvent
    public void worldRender(RenderWorldLastEvent event) {
        partialTicks = event.partialTicks;
    }

    @SubscribeEvent
    public void keybind(InputEvent.KeyInputEvent event) {
        if (KeybindNameHistory.searchPlayer.isKeyDown()) {
            getMouseOver(partialTicks);

            if (targetEntity != null && targetEntity instanceof EntityPlayer) {
                if (targetEntity.getDisplayName().getFormattedText().contains(EnumChatFormatting.OBFUSCATED.toString())) {
                    return;
                }

                if (mc.currentScreen == null && mc.theWorld != null && mc.thePlayer != null) {
                    ModCore.getInstance().getGuiHandler().open(new ScreenHistory(targetEntity.getName(), false));
                }
            }
        }
    }

    public void getMouseOver(float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("patcher_pick");
            double distance = 128;
            double distanceFromEyes = 128;
            mc.objectMouseOver = entity.rayTrace(distance, partialTicks);
            Vec3 position = entity.getPositionEyes(partialTicks);
            if (mc.objectMouseOver != null) {
                distanceFromEyes = mc.objectMouseOver.hitVec.distanceTo(position);
            }

            Vec3 lookPosition = entity.getLook(partialTicks);
            double x = lookPosition.xCoord * distance;
            double y = lookPosition.yCoord * distance;
            double z = lookPosition.zCoord * distance;
            Vec3 vector = position.addVector(x, y, z);
            targetEntity = null;
            Vec3 objectPosition = null;
            List<Entity> entities = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(x, y, z).expand(1, 1, 1), null);
            double distanceFrom = distanceFromEyes;

            for (Entity worldEntities : entities) {
                float collisionBorder = worldEntities.getCollisionBorderSize();
                AxisAlignedBB boundingBox = worldEntities.getEntityBoundingBox().expand(collisionBorder, collisionBorder, collisionBorder);
                MovingObjectPosition movingObjectPosition = boundingBox.calculateIntercept(position, vector);
                if (boundingBox.isVecInside(position)) {
                    if (distanceFrom < 0.0) {
                        continue;
                    }

                    targetEntity = worldEntities;

                    if (movingObjectPosition == null) {
                        objectPosition = position;
                    } else {
                        objectPosition = movingObjectPosition.hitVec;
                    }

                    distanceFrom = 0;
                } else {
                    if (movingObjectPosition == null) {
                        continue;
                    }

                    double distanceTo = position.distanceTo(movingObjectPosition.hitVec);
                    if (distanceTo >= distanceFrom && distanceFrom != 0) {
                        continue;
                    }

                    if (worldEntities == entity.ridingEntity && !entity.canRiderInteract()) {
                        if (distanceFrom != 0) {
                            continue;
                        }

                        targetEntity = worldEntities;
                        objectPosition = movingObjectPosition.hitVec;
                    } else {
                        targetEntity = worldEntities;
                        objectPosition = movingObjectPosition.hitVec;
                        distanceFrom = distanceTo;
                    }
                }
            }

            if (targetEntity != null && objectPosition != null
                && position.distanceTo(objectPosition) > 12) {
                targetEntity = null;
                mc.objectMouseOver = new MovingObjectPosition(MovingObjectType.MISS, objectPosition, null, new BlockPos(objectPosition));
            }

            mc.mcProfiler.endSection();
        }
    }
}
