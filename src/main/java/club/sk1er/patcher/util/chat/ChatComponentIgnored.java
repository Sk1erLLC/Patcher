package club.sk1er.patcher.util.chat;

import net.minecraft.util.ChatComponentText;

public class ChatComponentIgnored extends ChatComponentText {
    public ChatComponentIgnored(String msg) {
        super(msg);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
