package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.util.chat.ChatHandler;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_CompactChat {
    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void patcher$appendMessageCounter(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHandler.appendMessageCounter(chatComponent, displayOnly);
    }

    @Inject(method = "setChatLine", at = @At("TAIL"))
    private void patcher$resetMessageHash(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        ChatHandler.resetMessageHash();
    }
}
