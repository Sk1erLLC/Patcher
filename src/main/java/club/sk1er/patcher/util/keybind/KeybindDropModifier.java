/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Used for dropping entire stacks on computers that don't allow for doing so, such as macOS.
 */
public class KeybindDropModifier extends KeyBinding {
    public KeybindDropModifier() {
        super("Drop stack modifier", Keyboard.KEY_NONE, "Patcher");
    }
}
