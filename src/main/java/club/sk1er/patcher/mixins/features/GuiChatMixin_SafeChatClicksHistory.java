package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiScreen.class)
public abstract class GuiChatMixin_SafeChatClicksHistory extends Gui {

    @ModifyArg(method = "handleComponentClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;sendChatMessage(Ljava/lang/String;Z)V"), index = 1)
    public boolean patcher$handleComponentClick(boolean addToChat) {

        return addToChat || (PatcherConfig.safeChatClicksHistory && ((Object) this) instanceof GuiChat);
    }
}
