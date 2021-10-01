package club.sk1er.patcher.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin_SpectatorShader {
    @Shadow
    private Minecraft mc;

    @Shadow
    public abstract void loadShader(ResourceLocation resourceLocationIn);

    @Redirect(
        method = "loadEntityShader",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;loadShader(Lnet/minecraft/util/ResourceLocation;)V"
        )
    )
    public void patcher$fixSpectatorShader(EntityRenderer entityRenderer, ResourceLocation resourceLocationIn) {
        if (mc.gameSettings.thirdPersonView == 0) loadShader(resourceLocationIn);
    }
}
