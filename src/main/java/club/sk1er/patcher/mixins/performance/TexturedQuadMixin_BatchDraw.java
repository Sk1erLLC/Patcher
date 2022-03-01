package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.WorldRendererAccessor;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TexturedQuad.class)
public class TexturedQuadMixin_BatchDraw {

    //#if MC==10809
    @Unique
    private boolean patcher$drawOnSelf;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"))
    private void patcher$beginDraw(WorldRenderer renderer, int glMode, VertexFormat format) {
        this.patcher$drawOnSelf = !((WorldRendererAccessor) renderer).isDrawing();
        if (this.patcher$drawOnSelf || !PatcherConfig.batchModelRendering) {
            renderer.begin(glMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void patcher$endDraw(Tessellator tessellator) {
        if (this.patcher$drawOnSelf || !PatcherConfig.batchModelRendering) {
            tessellator.draw();
        }
    }
    //#endif
}
