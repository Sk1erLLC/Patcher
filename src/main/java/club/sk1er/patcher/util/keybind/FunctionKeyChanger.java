package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class FunctionKeyChanger {
    public static class KeybindHideScreen extends KeyBinding {
        public KeybindHideScreen() {
            super("Hide Screen", Keyboard.KEY_F1, "Patcher");
        }
    }

    public static class KeybindDebugView extends KeyBinding {
        public KeybindDebugView() {
            super("Custom F3", Keyboard.KEY_F3, "Patcher");
        }
    }

    public static class KeybindClearShaders extends KeyBinding {
        public KeybindClearShaders() {
            super("Clear Vanilla Shaders", Keyboard.KEY_F4, "Patcher");
        }
    }
}
