package club.sk1er.patcher.mixins.performance.forge;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;
import java.util.Set;

@Mixin(FluidRegistry.class)
public class FluidRegistryMixin_Optimization {
    @Shadow(remap = false) static Set<Fluid> bucketFluids;

    /**
     * @author LlamaLad7
     * @reason Avoid making a copy of the set.
     */
    @Overwrite(remap = false)
    public static Set<Fluid> getBucketFluids() {
        return Collections.unmodifiableSet(bucketFluids);
    }
}
