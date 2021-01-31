package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindSprint extends KeyBinding {
    public KeybindSprint() {
        super("Toggle Sprint", Keyboard.KEY_NONE, "Patcher");
    }
}
