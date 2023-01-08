package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.util.world.render.culling.EntityCulling;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityMobSpawnerRenderer.class)
public class TileEntityMobSpawnerRendererMixin_EntityCulling {
    //#if MC<11200
    private static final String patcher$renderEntity = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntityWithPosYaw(Lnet/minecraft/entity/Entity;DDDFF)Z";
    //#else if MC >=11200
    //$$ private static final String patcher$renderEntity = "Lnet/minecraft/client/renderer/entity/RenderManager;doRenderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V";
    //#endif

    @Inject(method = "renderMob", at = @At(value = "INVOKE", target = patcher$renderEntity))
    private static void patcher$captureRenderingPre(CallbackInfo ci) {
        EntityCulling.renderingSpawnerEntity = true;
    }

    @Inject(method = "renderMob", at = @At(value = "INVOKE", target = patcher$renderEntity, shift = At.Shift.AFTER))
    private static void patcher$captureRenderingPost(CallbackInfo ci) {
        EntityCulling.renderingSpawnerEntity = false;
    }
}
