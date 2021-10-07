package club.sk1er.patcher.mixins.features.render.item;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin_FireRendering {
    @Shadow @Final private Minecraft mc;

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void patcher$changeHeightAndFixOverlay(float partialTicks, CallbackInfo ci) {
        if (this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1").getFrameCount() == 0) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, PatcherConfig.fireOverlayHeight, 0f);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("TAIL"))
    private void patcher$popMatrix(float partialTicks, CallbackInfo ci) {
        GlStateManager.popMatrix();
    }
}
