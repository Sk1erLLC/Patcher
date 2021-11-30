package club.sk1er.patcher.mixins.performance;

//#if MC==10809
import net.minecraft.client.Minecraft;
import net.minecraft.client.stream.IStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
//#endif
public class MinecraftMixin_SkipTwitchStuff {
    //#if MC==10809
    @Redirect(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V")
    )
    private void patcher$skipTwitchCode1(IStream instance) {
        // No-op
    }

    @Redirect(
        method = "runGameLoop",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V")
    )
    private void patcher$skipTwitchCode2(IStream instance) {
        // No-op
    }
    //#endif
}
