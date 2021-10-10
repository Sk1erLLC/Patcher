package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_TransparentChat extends Gui {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", opcode = 0))
    private void patcher$transparentChat(int left, int top, int right, int bottom, int color) {
        if (PatcherConfig.transparentChat) return;
        drawRect(left, top, right, bottom, color);
    }
}
