package club.sk1er.patcher.mixins.bugfixes.optifine;

import net.minecraft.client.renderer.RenderGlobal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin_FixSkyVBOs {
    @Dynamic("OptiFine")
    @Redirect(
        method = "renderSky(Lnet/minecraft/client/renderer/WorldRenderer;FZ)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;renderDistance:I",
            opcode = Opcodes.GETFIELD,
            remap = false
        )
    )
    private int patcher$distanceOverride(RenderGlobal instance) {
        return 256;
    }

    @Dynamic("OptiFine")
    @Redirect(
        method = "renderSky(FI)V",
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I")),
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;vboEnabled:Z",
            ordinal = 0
        )
    )
    private boolean patcher$fixVBO(RenderGlobal instance) {
        return false;
    }
}
