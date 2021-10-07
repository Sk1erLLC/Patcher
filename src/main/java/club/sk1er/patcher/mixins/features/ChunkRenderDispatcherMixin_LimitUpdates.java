package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.class)
public class ChunkRenderDispatcherMixin_LimitUpdates {
    @SuppressWarnings("BusyWait")
    @Inject(method = "getNextChunkUpdate", at = @At("HEAD"))
    private void patcher$limitChunkUpdates(CallbackInfoReturnable<ChunkCompileTaskGenerator> cir) throws InterruptedException {
        while (PatcherConfig.limitChunks && RenderChunk.renderChunksUpdated >= PatcherConfig.chunkUpdateLimit) {
            Thread.sleep(50L);
        }
    }
}
