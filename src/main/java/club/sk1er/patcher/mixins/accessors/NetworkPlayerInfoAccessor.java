package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkPlayerInfo.class)
public interface NetworkPlayerInfoAccessor {
    @Accessor
    void setLocationSkin(ResourceLocation location);
}
