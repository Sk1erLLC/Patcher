package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.chat.ChatUtilities;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatStyle.class)
public abstract class ChatStyleMixin_ModifyHoverEvent {

    @Shadow private HoverEvent chatHoverEvent;
    @Shadow protected abstract ChatStyle getParent();
    @Shadow private ClickEvent chatClickEvent;

    /**
     * @author asbyth
     * @reason Modify hover components with a click action to append what will happen on click
     */
    @Overwrite
    public HoverEvent getChatHoverEvent() {
        HoverEvent hoverEvent = this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
        if (!PatcherConfig.safeChatClicks) {
            return hoverEvent;
        }

        ClickEvent chatClickEvent = this.chatClickEvent;
        if (chatClickEvent == null) {
            return hoverEvent;
        }

        ClickEvent.Action action = chatClickEvent.getAction();

        if (!(action.equals(ClickEvent.Action.OPEN_FILE) || action.equals(ClickEvent.Action.OPEN_URL) || action.equals(ClickEvent.Action.RUN_COMMAND))) {
            return hoverEvent;
        }

        String actionMessage = action == ClickEvent.Action.RUN_COMMAND ? "Runs " : "Opens ";
        String msg = ChatUtilities.translate("&7" + actionMessage + "&e" + chatClickEvent.getValue() + " &7on click.");
        if (hoverEvent == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(msg));
        }

        if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
            ChatComponentText append = new ChatComponentText(msg);
            IChatComponent value = hoverEvent.getValue();

            if (value.getSiblings().contains(append) || value.getFormattedText().contains(msg)) {
                return hoverEvent;
            }

            IChatComponent copy = value.createCopy();
            copy.appendText("\n");
            copy.appendText(msg);
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, copy);
        }

        return hoverEvent;
    }
}
