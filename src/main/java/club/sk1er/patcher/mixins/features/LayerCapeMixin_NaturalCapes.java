package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerCape.class)
public class LayerCapeMixin_NaturalCapes {
    @Unique
    private AbstractClientPlayer patcher$entityLivingBaseIn;
    @Unique
    private float patcher$height;
    @Unique
    private float patcher$swing;
    @Unique
    private float patcher$swingSides;

    @Final
    @Shadow
    private RenderPlayer playerRenderer;

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
            if (this.patcher$entityLivingBaseIn.getCurrentArmor(2) != null) {
                z1 += 0.032;

            }
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
        patcher$swingSides = ori;
        return ori;
    }

    @Inject(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;sin(F)F", shift = At.Shift.AFTER, ordinal = 1),
        cancellable = true
    )
    public void patcher$replaceCapeRotations(AbstractClientPlayer entityLivingBaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        if (PatcherConfig.naturalCapes) {
            float v = (float) ((patcher$swingSides / 2) / Math.sqrt(2 + (Math.pow((patcher$swingSides - 10) / 60, 2))));
            float min = entityLivingBaseIn.isSneaking() ? (entityLivingBaseIn.getCurrentArmor(2) != null || entityLivingBaseIn.getCurrentArmor(3) != null) ? 41.0F : 35.0F : 5.0F;

            float angle1 = MathHelper.clamp_float((patcher$swing / ((float) Math.sqrt(6 + (Math.pow(patcher$swing / 150, 2))))), min, 130.0F) + patcher$height;
            float angle2 = MathHelper.clamp_float(v, -50.0F, 65.0F);
            float angle3 = MathHelper.clamp_float(-v, -50.0F, 65.0F);

            GlStateManager.rotate(angle1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(angle2, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(angle3, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

            playerRenderer.getMainModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
            ci.cancel();
        }
    }
}
