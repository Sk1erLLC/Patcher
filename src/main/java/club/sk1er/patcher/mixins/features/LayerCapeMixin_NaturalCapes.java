package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC==11202
//$$ import net.minecraft.inventory.EntityEquipmentSlot;
//#endif
@Mixin(LayerCape.class)
public class LayerCapeMixin_NaturalCapes {
    @Unique
    private AbstractClientPlayer patcher$entityLivingBaseIn;
    @Unique
    private float patcher$height;
    @Unique
    private float patcher$swing;
    @Unique
    private float patcher$v;

    @ModifyConstant(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        constant = @Constant(floatValue = 0.0F, ordinal = 2)
    )
    public float patcher$disableSwingClampValue(float original) {
        if (PatcherConfig.naturalCapes) {
            return -Float.MAX_VALUE;
        }
        return original;
    }

    @Inject(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "HEAD")
    )
    public void patcher$setEntityLivingBaseIn(AbstractClientPlayer entityLivingBaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        this.patcher$entityLivingBaseIn = entityLivingBaseIn;
    }

    @Redirect(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V")
    )
    public void patcher$replaceGLStateManagerTranslate(float x, float y, float z) {
        if (PatcherConfig.naturalCapes) {
            float y1 = 0.00F;
            float z1 = 0.125F;
            if (this.patcher$entityLivingBaseIn.isSneaking()) {
                z1 = 0.027F;
                y1 = 0.05F;
            }
            //#if MC==10809
            if (patcher$entityLivingBaseIn.getCurrentArmor(2) != null || patcher$entityLivingBaseIn.getCurrentArmor(3) != null) {
                z1 += 0.032;

            }
            //#else
            //$$if (this.patcher$entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null || this.patcher$entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null) {
            //$$z1 += 0.032;
            //$$}
            //#endif

            GlStateManager.translate(0.0F, y1, z1);
            return;
        }
        GlStateManager.translate(x, y, z);
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 8,
        at = @At(value = "STORE")
    )
    public float patcher$setHeight(float ori) {
        patcher$height = ori;
        return ori;
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 9,
        at = @At(value = "STORE")
    )
    public float patcher$setSwing(float ori) {
        patcher$swing = ori;
        return ori;
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 10,
        at = @At(value = "STORE")
    )
    public float patcher$setSwingSides(float ori) {
        patcher$v = (float) ((ori / 2) / Math.sqrt(2 + (Math.pow((ori - 10) / 60, 2))));
        return ori;
    }

    @ModifyArg(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 0),
        index = 0
    )
    private float patcher$modifyCapeRotation1(float angle) {
        if (!PatcherConfig.naturalCapes) {
            return angle;
        }
        float min;
        if (patcher$entityLivingBaseIn.isSneaking()) {
            min = 8.0f;
            //#if MC==10809
            if ((patcher$entityLivingBaseIn.getCurrentArmor(2) != null || patcher$entityLivingBaseIn.getCurrentArmor(3) != null)) {
                min += 3.0F;
            }
            //#else
            //$$if (this.patcher$entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null || this.patcher$entityLivingBaseIn.getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null) {
            //$$min+=10.0F;
            //$$}
            //#endif

        } else {
            min = 5;
        }

        return MathHelper.clamp_float((patcher$swing / ((float) Math.sqrt(6 + (Math.pow(patcher$swing / 150, 2))))), min, 130.0F) + patcher$height;
    }

    @ModifyArg(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 1),
        index = 0
    )
    private float patcher$modifyCapeRotation2(float angle) {
        if (!PatcherConfig.naturalCapes) {
            return angle;
        }
        return MathHelper.clamp_float(patcher$v, -50.0F, 65.0F);
    }

    @ModifyArg(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 2),
        index = 0
    )
    private float patcher$modifyCapeRotation3(float angle) {
        if (!PatcherConfig.naturalCapes) {
            return angle;
        }
        return MathHelper.clamp_float(-patcher$v, -50.0F, 65.0F);
    }
}
