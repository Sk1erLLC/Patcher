package club.sk1er.patcher.mixins.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_KeepChatMessages {

    private final String patcher$clearChatMessagesTarget =
        //#if MC==10809
        "Lnet/minecraft/client/gui/GuiNewChat;clearChatMessages()V";
        //#else
        //$$ "Lnet/minecraft/client/gui/GuiNewChat;clearChatMessages(Z)V";
        //#endif

    @Redirect(method = "displayGuiScreen", at = @At(value = "INVOKE", target = patcher$clearChatMessagesTarget))
    private void patcher$keepChatMessages(GuiNewChat instance) {
        // No-op
    }
}
