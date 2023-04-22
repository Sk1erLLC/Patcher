package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.At;
import java.awt.Desktop;
//#if MC==10809
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
//#endif

//#if MC==11202
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import net.minecraft.client.renderer.OpenGlHelper;
//$$ import net.minecraft.util.Util;
//$$ import java.io.File;
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

    //#if MC==11202
    //$$ @Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;openFile(Ljava/io/File;)V"))
    //$$ private void patcher$fixFolderOpening(File file) {
    //$$    if (Util.getOSType() == Util.EnumOS.WINDOWS) {
    //$$        try {
    //$$            Desktop.getDesktop().open(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks());
    //$$        } catch (Exception ignored) {}
    //$$    } else {
    //$$        OpenGlHelper.openFile(file);
    //$$    }
    //$$ }
    //#endif

}
