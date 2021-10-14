package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.util.world.render.culling.ParticleCulling;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin_ParticleCulling {
    @Redirect(
        method = "renderParticles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/EntityFX;renderParticle(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V"
        )
    )
    private void patcher$cullParticles(EntityFX instance, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (ParticleCulling.shouldRender(instance)) {
            instance.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }
    }

    @ModifyVariable(
        method = "updateEffectAlphaLayer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;tickParticle(Lnet/minecraft/client/particle/EntityFX;)V", shift = At.Shift.AFTER)
    )
    private EntityFX patcher$checkIfCulled(EntityFX entityFX) {
        if (ParticleCulling.camera != null) {
            entityFX.distanceWalkedModified = ParticleCulling.camera.isBoundingBoxInFrustum(entityFX.getEntityBoundingBox()) ? 1.0F : -1.0F;
        }
        return entityFX;
    }
}
