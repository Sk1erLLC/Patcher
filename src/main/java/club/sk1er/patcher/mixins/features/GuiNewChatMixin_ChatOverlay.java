package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_ChatOverlay {
    @Shadow @Final private Minecraft mc;

    @ModifyVariable(method = "drawChat", at = @At("HEAD"), argsOnly = true)
    private int patcher$modifyUpdateCounter(int updateCounter) {
        return Patcher.instance.getChatPeek().isKeyDown() ? 0 : updateCounter;
    }

    @ModifyVariable(method = "drawChat", at = @At("STORE"), index = 2)
    private int patcher$modifyChatLineLimit(int linesToDraw) {
        return Patcher.instance.getChatPeek().isKeyDown()
            ? GuiNewChat.calculateChatboxHeight(mc.gameSettings.chatHeightFocused) / 9
            : linesToDraw;
    }
}
