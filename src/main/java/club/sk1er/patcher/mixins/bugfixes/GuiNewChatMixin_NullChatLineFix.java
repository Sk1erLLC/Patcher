package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_NullChatLineFix {
    @Redirect(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int patcher$checkIfChatLineIsNull(ChatLine instance) {
        if (instance == null) return -1;
        return instance.getChatLineID();
    }
}
