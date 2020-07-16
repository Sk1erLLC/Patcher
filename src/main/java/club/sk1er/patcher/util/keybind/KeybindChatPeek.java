package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindChatPeek extends KeyBinding {
    public KeybindChatPeek() {
        super("Chat Peek", Keyboard.KEY_NONE, "Patcher");
    }
}
