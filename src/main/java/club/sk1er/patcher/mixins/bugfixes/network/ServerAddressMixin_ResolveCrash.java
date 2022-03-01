package club.sk1er.patcher.mixins.bugfixes.network;

import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.IDN;

@Mixin(ServerAddress.class)
public class ServerAddressMixin_ResolveCrash {
    @Shadow @Final private String ipAddress;

    //#if MC==10809
    /**
     * @author LlamaLad7
     * @reason Fix crash - MC-89698
     */
    @Overwrite
    public String getIP() {
        try {
            return IDN.toASCII(this.ipAddress);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
    //#endif
}
