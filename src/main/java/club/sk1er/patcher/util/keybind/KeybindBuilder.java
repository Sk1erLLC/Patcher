package club.sk1er.patcher.util.keybind;

import club.sk1er.mods.core.key.ModCoreKeybinding;
import org.lwjgl.input.Keyboard;

public class KeybindBuilder {

    private ModCoreKeybinding nameHistory;
    private ModCoreKeybinding dropEntireStack;
    public static final KeybindBuilder instance = new KeybindBuilder();

    public void registerPatcherKeybinds() {
        nameHistory = new ModCoreKeybinding("NAME_HISTORY", "Patcher", Keyboard.KEY_N);
        dropEntireStack = new ModCoreKeybinding("DROP_ENTIRE_STACK", "Patcher", Keyboard.KEY_NONE);
    }

    public ModCoreKeybinding getDropEntireStack() {
        return dropEntireStack;
    }

    public ModCoreKeybinding getNameHistory() {
        return nameHistory;
    }
}
