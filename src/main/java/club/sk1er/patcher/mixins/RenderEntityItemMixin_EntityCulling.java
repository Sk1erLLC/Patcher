package club.sk1er.patcher.mixins;

import club.sk1er.patcher.util.world.render.culling.EntityCulling;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public class RenderEntityItemMixin_EntityCulling {
    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private void patcher$checkRenderState(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (EntityCulling.renderItem(entity)) {
            ci.cancel();
        }
    }
}
