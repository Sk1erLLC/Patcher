package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiNewChat.class)
public interface GuiNewChatAccessor {
    @Accessor
    List<ChatLine> getChatLines();

    @Accessor
    List<ChatLine> getDrawnChatLines();
}
