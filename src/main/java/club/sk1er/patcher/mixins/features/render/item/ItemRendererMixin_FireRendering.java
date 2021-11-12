package club.sk1er.patcher.mixins.features.render.item;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
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

    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
    private void patcher$enableFireOpacity(float partialTicks, CallbackInfo ci) {
        float fireOpacity = patcher$getFireOpacity(partialTicks);
        if (fireOpacity == 1.0f) return;
        GlStateManager.color(1, 1, 1, fireOpacity);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    private void patcher$disableFireOpacity(CallbackInfo ci) {
        GlStateManager.color(1, 1, 1, 1);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("TAIL"))
    private void patcher$popMatrix(float partialTicks, CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    private float patcher$getFireOpacity(float partialTicks) {
        float fireOpacity = PatcherConfig.fireOverlayOpacity;
        if (PatcherConfig.hideFireOverlayWithFireResistance && mc.thePlayer.isPotionActive(Potion.fireResistance)) {
            int duration = mc.thePlayer.getActivePotionEffect(Potion.fireResistance).getDuration();
            fireOpacity *= duration > 100 ? 0.0F : 0.5F - MathHelper.sin(((float)duration - partialTicks) * (float)Math.PI * 0.2F) * 0.5F;
        }
        return fireOpacity;
    }
}
