package club.sk1er.patcher.hooks;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CapeLayerHook {
    public static void doRenderLayerHook(RenderPlayer playerRenderer, AbstractClientPlayer entityLivingBaseIn, float partialTicks) {
        if (entityLivingBaseIn.hasPlayerInfo() && !entityLivingBaseIn.isInvisible() && entityLivingBaseIn.isWearing(EnumPlayerModelParts.CAPE) && entityLivingBaseIn.getLocationCape() != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            playerRenderer.bindTexture(entityLivingBaseIn.getLocationCape());
            GlStateManager.pushMatrix();

            float y = 0.00F;
            float x = 0.125F;
            if (entityLivingBaseIn.isSneaking()) {
                x = 0.027F;
                y = 0.05F;
            }

            GlStateManager.translate(0.0F, y, x);

            double xDiff = entityLivingBaseIn.prevChasingPosX + (entityLivingBaseIn.chasingPosX - entityLivingBaseIn.prevChasingPosX) * partialTicks - (entityLivingBaseIn.prevPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.prevPosX) * partialTicks);
            double yDiff = entityLivingBaseIn.prevChasingPosY + (entityLivingBaseIn.chasingPosY - entityLivingBaseIn.prevChasingPosY) * partialTicks - (entityLivingBaseIn.prevPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.prevPosY) * partialTicks);
            double zDiff = entityLivingBaseIn.prevChasingPosZ + (entityLivingBaseIn.chasingPosZ - entityLivingBaseIn.prevChasingPosZ) * partialTicks - (entityLivingBaseIn.prevPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.prevPosZ) * partialTicks);
            float camYawDiff = entityLivingBaseIn.prevCameraYaw + (entityLivingBaseIn.cameraYaw - entityLivingBaseIn.prevCameraYaw) * partialTicks;
            float yawDiff = entityLivingBaseIn.prevRenderYawOffset + (entityLivingBaseIn.renderYawOffset - entityLivingBaseIn.prevRenderYawOffset) * partialTicks;

            double leftSwing = MathHelper.sin(yawDiff * 0.017453292F);
            double rightSwing = -MathHelper.cos(yawDiff * 0.017453293F);
            float height = (float) yDiff * 10.0F;
            float swing = (float) (xDiff * leftSwing + zDiff * rightSwing) * 100.0F;
            float swingSides = (float) (xDiff * rightSwing - zDiff * leftSwing) * 100.0F;
            height += MathHelper.sin((entityLivingBaseIn.prevDistanceWalkedModified + (entityLivingBaseIn.distanceWalkedModified - entityLivingBaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * camYawDiff;

            float angle1 = MathHelper.clamp_float((swing * 0.8F) / ((float) Math.sqrt(1.5 + Math.pow(swing / 120, 2))) + height, entityLivingBaseIn.isSneaking() ? 35.0F + height : 5.1F, 122.0F + height);
            float angle2 = MathHelper.clamp_float(swingSides / 3.60F, -20.0F, 20.0F);
            float angle3 = MathHelper.clamp_float(-swingSides / 3.60F, -20.0F, 20.0F);
            GlStateManager.rotate(angle1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(angle2, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(angle3, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
        }
    }
}
