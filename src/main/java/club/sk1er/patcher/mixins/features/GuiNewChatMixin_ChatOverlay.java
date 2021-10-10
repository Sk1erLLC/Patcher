package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.screen.render.overlay.OverlayHandler;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_ChatOverlay {
    @ModifyVariable(method = "drawChat", at = @At("HEAD"), argsOnly = true)
    private int patcher$modifyUpdateCounter(int updateCounter) {
        return OverlayHandler.toggledChat ? 0 : updateCounter;
    }
}
