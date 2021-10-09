package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.MinecraftServerHook;
import club.sk1er.patcher.screen.render.overlay.metrics.MetricsData;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin_Metrics {
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void patcher$createMetricsData(CallbackInfo ci) {
        MinecraftServerHook.metricsData = new MetricsData();
    }

    @Redirect(
        method = "tick",
        at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;tickTimeArray:[J", args = "array=set")
    )
    private void patcher$pushMetricsSample(long[] array, int index, long value) {
        array[index] = value;
        MinecraftServerHook.metricsData.pushSample(value);
    }
}
