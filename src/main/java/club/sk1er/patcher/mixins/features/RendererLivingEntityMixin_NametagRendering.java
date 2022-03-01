package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.NameTagRenderingHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin_NametagRendering<T extends EntityLivingBase> extends Render<T> {
    protected RendererLivingEntityMixin_NametagRendering(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private void handleBetterF1AndShowOwnNametag(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (PatcherConfig.betterHideGui && !Minecraft.isGuiEnabled()) cir.setReturnValue(false);
        else if (entity == renderManager.livingPlayer && !entity.isInvisible() && PatcherConfig.showOwnNametag) cir.setReturnValue(true);
    }

    //#if MC==10809
    @Redirect(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int patcher$useShadowedNametagRendering(FontRenderer instance, String text, int x, int y, int color) {
        return NameTagRenderingHooks.drawNametagText(instance, text, x, y, color);
    }
    //#endif
}
