package club.sk1er.patcher.mixins.bugfixes.render.entity;

import club.sk1er.patcher.asm.external.mods.optifine.RenderTransformer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RendererLivingEntity.class)
public class RendererLivingEntityMixin_NametagPerspective {
    @Redirect(
        method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;playerViewX:F")
    )
    private float patcher$fixNametagPerspective(RenderManager instance) {
        //TODO: Migrate the rest of the transformers to Mixins
        return instance.playerViewX * RenderTransformer.checkPerspective();
    }
}
