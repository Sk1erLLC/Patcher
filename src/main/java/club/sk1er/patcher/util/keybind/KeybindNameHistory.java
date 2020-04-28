package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindNameHistory extends KeyBinding {
    public KeybindNameHistory() {
        super("Name History", Keyboard.KEY_N, "Patcher");
    }
}
