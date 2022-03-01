package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin_BatchDrawing {
    //#if MC==10809
    @Shadow private boolean compiled;

    private boolean patcher$compiledState;

    @Inject(method = "render", at = @At("HEAD"))
    private void patcher$resetCompiled(float j, CallbackInfo ci) {
        if (patcher$compiledState != PatcherConfig.batchModelRendering) {
            this.compiled = false;
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/Tessellator;getWorldRenderer()Lnet/minecraft/client/renderer/WorldRenderer;"))
    private void patcher$beginRendering(CallbackInfo ci) {
        this.patcher$compiledState = PatcherConfig.batchModelRendering;
        if (PatcherConfig.batchModelRendering) {
            Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEndList()V", remap = false))
    private void patcher$draw(CallbackInfo ci) {
        if (PatcherConfig.batchModelRendering) {
            Tessellator.getInstance().draw();
        }
    }
    //#endif
}
