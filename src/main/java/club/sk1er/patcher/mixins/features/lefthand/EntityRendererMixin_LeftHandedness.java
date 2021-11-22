package club.sk1er.patcher.mixins.features.lefthand;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.ItemRendererHook;
import club.sk1er.patcher.mixins.accessors.ItemRendererAccessor;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_LeftHandedness {
    @Shadow @Final public ItemRenderer itemRenderer;

    @Dynamic("OptiFine adds its own version of renderHand")
    @Inject(
        method = {"renderHand(FI)V", "renderHand(FIZZZ)V", "func_78476_b"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;enableLightmap()V", shift = At.Shift.AFTER)
    )
    private void patcher$flipHandSide(CallbackInfo ci) {
        ItemRendererHook.isRenderingItemInFirstPerson = true;
        if (PatcherConfig.leftHandInFirstPerson) {
            ItemStack itemToRender = ((ItemRendererAccessor) itemRenderer).getItemToRender();
            if (itemToRender == null || !(itemToRender.getItem() instanceof ItemMap)) {
                GlStateManager.scale(-1, 1, 1);
                GL11.glFrontFace(GL11.GL_CW);
            }
        }
    }

    @Dynamic("OptiFine adds its own version of renderHand")
    @Inject(
        method = {"renderHand(FI)V", "renderHand(FIZZZ)V", "func_78476_b"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;disableLightmap()V")
    )
    private void patcher$resetFrontFace(CallbackInfo ci) {
        if (PatcherConfig.leftHandInFirstPerson) {
            GL11.glFrontFace(GL11.GL_CCW);
        }
    }
}
