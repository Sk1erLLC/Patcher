package club.sk1er.patcher.mixins.bugfixes.crashes;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin_ResolveCrash {

    @Shadow @Final private List<ServerListEntryLanDetected> serverListLan;
    @Shadow @Final private GuiListExtended.IGuiListEntry lanScanEntry;

    @Inject(
        method = "getListEntry",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/ServerSelectionList;serverListLan:Ljava/util/List;"),
        cancellable = true
    )
    private void patcher$resolveIndexError(int index, CallbackInfoReturnable<GuiListExtended.IGuiListEntry> cir) {
        if (index >= this.serverListLan.size()) {
            cir.setReturnValue(this.lanScanEntry);
        }
    }
}
