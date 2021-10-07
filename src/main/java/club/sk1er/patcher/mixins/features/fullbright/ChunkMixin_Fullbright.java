package club.sk1er.patcher.mixins.features.fullbright;

import club.sk1er.patcher.util.world.render.FullbrightTicker;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class)
public class ChunkMixin_Fullbright {

    @Inject(method = {"getLightFor", "getLightSubtracted"}, at = @At("HEAD"), cancellable = true)
    private void patcher$fullbright(CallbackInfoReturnable<Integer> cir) {
        // todo: should this be checking main thread like World's fullbright does?
        //  or is world accidentally checking main thread?
        if (FullbrightTicker.isFullbright()) {
            cir.setReturnValue(15);
        }
    }
}
