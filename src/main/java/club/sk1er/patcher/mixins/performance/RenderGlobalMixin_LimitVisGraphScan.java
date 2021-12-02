package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.ducks.VisGraphExt;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin_LimitVisGraphScan {
    @Inject(method = "getVisibleFacings", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/chunk/VisGraph;<init>()V", shift = At.Shift.AFTER, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void patcher$setLimitScan(CallbackInfoReturnable<Set<EnumFacing>> cir, VisGraph visgraph) {
        ((VisGraphExt) visgraph).patcher$setLimitScan(true);
    }
}
