package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindDropStack extends KeyBinding {
    public KeybindDropStack() {
        super("Drop entire stack", Keyboard.KEY_NONE, "Patcher");
    }
}
