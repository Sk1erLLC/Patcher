package club.sk1er.patcher.mixins.accessors;

//#if MC==10809
import com.google.common.cache.LoadingCache;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.gen.Accessor;
//#endif
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftForgeClient.class)
public interface MinecraftForgeClientAccessor {
    //#if MC==10809
    @SuppressWarnings("UnstableApiUsage")
    @Accessor(remap = false)
    static LoadingCache<Pair<World, BlockPos>, RegionRenderCache> getRegionCache() {
        throw new AssertionError();
    }
    //#endif
}
