package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.ducks.VisGraphExt;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin_LimitVisGraphScan {

    @ModifyVariable(method = "getVisibleFacings", name = "visgraph", at = @At(value = "STORE", ordinal = 0))
    private VisGraph patcher$setLimitScan(VisGraph visgraph) {
        ((VisGraphExt) visgraph).patcher$setLimitScan(true);
        return visgraph;
    }

}
