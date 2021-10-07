package club.sk1er.patcher.mixins.features.fullbright;

import club.sk1er.patcher.util.world.render.FullbrightTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin_Fullbright {

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void patcher$checkLightFor_fullbright(CallbackInfoReturnable<Boolean> cir) {
        if (this.patcher$checkFullbright()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {
        "getLightFromNeighborsFor", "getLightFromNeighbors", "getRawLight",
        "getLight(Lnet/minecraft/util/BlockPos;)I", "getLight(Lnet/minecraft/util/BlockPos;Z)I"
    }, at = @At("HEAD"), cancellable = true)
    private void patcher$getLight_fullbright(CallbackInfoReturnable<Integer> cir) {
        if (this.patcher$checkFullbright()) {
            cir.setReturnValue(15);
        }
    }

    @Unique
    private boolean patcher$checkFullbright() {
        return Minecraft.getMinecraft().isCallingFromMinecraftThread() && FullbrightTicker.isFullbright();
    }
}
