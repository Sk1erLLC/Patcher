package club.sk1er.patcher.mixins.bugfixes.optifine;

import net.minecraft.client.renderer.RenderGlobal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin_FixSkyVBOs {
    @Dynamic("OptiFine")
    @Redirect(method = "renderSky(Lnet/minecraft/client/renderer/WorldRenderer;FZ)V", at = @At(value ="FIELD", target = "net/minecraft/client/renderer/RenderGlobal.renderDistance:I", opcode = Opcodes.GETFIELD))
    private int patcher$distanceOverride(RenderGlobal instance) {
        return 256;
    }

    @Dynamic("OptiFine")
    @Redirect(method = "renderSky(FI)V", at = @At(value = "FIELD", target = "net/minecraft/client/renderer/RenderGlobal.vboEnabled:Z", opcode = Opcodes.GETFIELD, ordinal = 3))
    private boolean patcher$fixVBO(RenderGlobal instance) {
        return false;
    }
}
