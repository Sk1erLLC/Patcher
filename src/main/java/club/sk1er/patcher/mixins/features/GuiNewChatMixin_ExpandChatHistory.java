package club.sk1er.patcher.mixins.features;

import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_ExpandChatHistory {
    @ModifyConstant(method = "setChatLine", constant = @Constant(intValue = 100))
    private int patcher$expandChatHistory(int original) {
        return 32767;
    }
}
