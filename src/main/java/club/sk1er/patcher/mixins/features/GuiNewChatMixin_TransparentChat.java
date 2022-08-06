package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import gg.essential.lib.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin_TransparentChat extends Gui {
    @WrapWithCondition(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private boolean patcher$transparentChat(int left, int top, int right, int bottom, int color) {
        return !PatcherConfig.transparentChat;
    }
}
