package club.sk1er.patcher.mixins.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_KeepChatMessages {
    @Redirect(method = "displayGuiScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;clearChatMessages()V"))
    private void patcher$keepChatMessages(GuiNewChat instance) {
        // No-op
    }
}
