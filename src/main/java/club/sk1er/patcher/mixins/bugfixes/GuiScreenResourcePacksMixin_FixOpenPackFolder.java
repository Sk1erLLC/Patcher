package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
//#if MC==10809
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Desktop;
import java.io.IOException;
//#endif

@Mixin(GuiScreenResourcePacks.class)
public class GuiScreenResourcePacksMixin_FixOpenPackFolder {
    //#if MC==10809
    @Inject(
        method = "actionPerformed",
        at =
        @At(
            value = "INVOKE",
            target = "Ljava/lang/Runtime;getRuntime()Ljava/lang/Runtime;",
            ordinal = 1,
            shift = At.Shift.BEFORE),
        cancellable = true)
    private void patcher$fixFolderOpening(CallbackInfo ci) throws IOException {
        Desktop.getDesktop().open(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks());
        ci.cancel();
    }
    //#endif
}
