package club.sk1er.patcher.keybind;

import club.sk1er.patcher.util.keybind.KeybindBuilder;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeybindNameHistory {

    public static final KeyBinding searchPlayer = KeybindBuilder.buildKeybind(
        "User Name History",
        Keyboard.KEY_N
    );
}
