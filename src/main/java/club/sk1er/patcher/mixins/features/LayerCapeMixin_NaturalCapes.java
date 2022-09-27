package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.LayerCapeHook;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerCape.class)
public class LayerCapeMixin_NaturalCapes {

    public AbstractClientPlayer entitylivingbaseIn;
    @Final
    @Shadow
    private RenderPlayer playerRenderer;
    private float o;
    private float p;
    private float q;

    @Inject(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "HEAD"))
    public void doRenderLayer$setEntitylivingbaseIn(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        this.entitylivingbaseIn = entitylivingbaseIn;
    }

    @Redirect(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"))
    public void naturalCapesTranslate(float x, float y, float z) {
        if (PatcherConfig.naturalCapes) {
            float y1 = 0.00F;
            float z1 = 0.125F;
            if (entitylivingbaseIn.isSneaking()) {
                z1 = 0.027F;
                y1 = 0.05F;
            }
            GlStateManager.translate(0.0F, y1, z1);
        }
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 8,
        at = @At(value = "STORE"))
    public float doRenderLayer$seto(float ori) {
        o = ori;
        return ori;
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 9,
        at = @At(value = "STORE"))
    public float doRenderLayer$setp(float ori) {
        p = ori;
        return ori;
    }

    @ModifyVariable(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        ordinal = 10,
        at = @At(value = "STORE"))
    public float doRenderLayer$setq(float ori) {
        q = ori;
        return ori;
    }

    @Inject(
        method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isSneaking()Z", shift = At.Shift.BEFORE),
        cancellable = true
    )
    public void doRenderLayerRotates(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        if (PatcherConfig.naturalCapes) {
            LayerCapeHook.rotate(playerRenderer, entitylivingbaseIn, o, p, q);
            ci.cancel();
        }
    }
}
