package club.sk1er.patcher.mixins.misc;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiOverlayDebug.class)
public class GuiOverlayDebugMixin_ShowPatcherVersion {
    @ModifyVariable(
        method = "getDebugInfoRight",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z",
            shift = At.Shift.AFTER,
            remap = false
        )
    )
    private List<String> patcher$showPatcherVersion(List<String> list) {
        list.add("Patcher " + Patcher.VERSION);
        return list;
    }
}
