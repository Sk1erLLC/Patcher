package club.sk1er.patcher.mixins;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerListEntryNormal.class)
public abstract class ServerListEntryNormalMixin_ResolveCrash {

    @Shadow protected abstract void prepareServerIcon();
    @Shadow @Final private ServerData server;

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ServerListEntryNormal;prepareServerIcon()V"))
    private void patcher$resolveCrash(ServerListEntryNormal serverListEntryNormal) {
        try {
            prepareServerIcon();
        } catch (Exception e) {
            Patcher.instance.getLogger().error("Failed to prepare server icon, setting to default.", e);
            server.setBase64EncodedIconData(null);
        }
    }
}
