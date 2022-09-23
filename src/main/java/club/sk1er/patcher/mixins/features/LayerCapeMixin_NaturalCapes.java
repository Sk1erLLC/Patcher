package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.CapeLayerHook;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerCape.class)
public class LayerCapeMixin_NaturalCapes {
    @Final
    @Shadow
    private RenderPlayer playerRenderer;

    @Inject(method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, CallbackInfo ci) {
        if (PatcherConfig.naturalCapes){
            CapeLayerHook.doRenderLayerHook(playerRenderer, entitylivingbaseIn, partialTicks);
            ci.cancel();
        }
    }
}
