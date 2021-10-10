package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.hooks.GuiNewChatHook;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_ChatDelay {
    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getLineCount()I"))
    private void patcher$processMessageQueue(int updateCounter, CallbackInfo ci) {
        GuiNewChatHook.processMessageQueue();
    }

    @Inject(method = "clearChatMessages", at = @At("HEAD"))
    private void patcher$clearMessageQueue(CallbackInfo ci) {
        GuiNewChatHook.messageQueue.clear();
    }
}
