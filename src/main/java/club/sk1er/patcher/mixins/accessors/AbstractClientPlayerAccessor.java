package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AbstractClientPlayerAccessor {
    @Accessor
    NetworkPlayerInfo getPlayerInfo();

    @Accessor
    void setPlayerInfo(NetworkPlayerInfo info);
}
