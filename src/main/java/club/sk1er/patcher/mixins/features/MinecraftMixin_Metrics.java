package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.MinecraftHook;
import club.sk1er.patcher.screen.render.overlay.metrics.MetricsData;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_Metrics {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void patcher$createMetricsData(CallbackInfo ci) {
        MinecraftHook.metricsData = new MetricsData();
    }

    @ModifyArg(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FrameTimer;addFrame(J)V")
    )
    private long patcher$pushMetricsSample(long time) {
        MinecraftHook.metricsData.pushSample(time);
        return time;
    }
}
