package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Used for checking a user's name history, by looking at them and pressing the keybind.
 * Only works if the player is within 12 blocks of our player.
 */
public class KeybindNameHistory extends KeyBinding {
    public KeybindNameHistory() {
        super("Name History", Keyboard.KEY_NONE, "Patcher");
    }
}
