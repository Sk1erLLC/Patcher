package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Desktop;

//#if MC==10809
import java.io.IOException;
//#endif

//#if MC==11202
//$$ import net.minecraft.util.Util;
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
    //$$ @Inject(
    //$$     method = "actionPerformed",
    //$$     at =
    //$$     @At(
    //$$         value = "INVOKE",
    //$$         target = "Lnet/minecraft/client/renderer/OpenGlHelper;openFile(Ljava/io/File;)V",
    //$$         shift = At.Shift.BEFORE),
    //$$     cancellable = true)
    //$$ private void patcher$fixFolderOpening(CallbackInfo ci) {
    //$$     if (Util.getOSType() == Util.EnumOS.WINDOWS) {
    //$$         try {
    //$$             Desktop.getDesktop().open(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks());
    //$$         } catch (Exception ignored) {}
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //#endif

}
