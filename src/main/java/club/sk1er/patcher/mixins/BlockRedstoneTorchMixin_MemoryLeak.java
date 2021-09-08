package club.sk1er.patcher.mixins;

import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(BlockRedstoneTorch.class)
public class BlockRedstoneTorchMixin_MemoryLeak {

    @Shadow private static Map<World, List<BlockRedstoneTorch.Toggle>> toggles;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void patcher$changeMapType(CallbackInfo ci) {
        toggles = new WeakHashMap<>();
    }
}
