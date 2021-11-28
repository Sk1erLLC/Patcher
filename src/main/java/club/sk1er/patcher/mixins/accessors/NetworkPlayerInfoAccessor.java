package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC==11202
//$$ import java.util.Map;
//$$ import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
//#endif

@Mixin(NetworkPlayerInfo.class)
public interface NetworkPlayerInfoAccessor {
    @Accessor
    //#if MC==10909
    void setLocationSkin(ResourceLocation location);
    //#else
    //$$ Map<Type, ResourceLocation> getPlayerTextures();
    //#endif
}
