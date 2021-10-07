package club.sk1er.patcher.mixins.performance;

import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(BlockRedstoneTorch.class)
public class BlockRedstoneTorchMixin_MemoryLeak {
    @Shadow private static Map<World, List<BlockRedstoneTorch.Toggle>> toggles = new WeakHashMap<>();
}
