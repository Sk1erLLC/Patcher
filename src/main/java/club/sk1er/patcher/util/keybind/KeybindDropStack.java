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
 * Used for dropping entire stacks on press
 * TODO: Fix, currently doesn't work.
 */
public class KeybindDropStack extends KeyBinding {
    public KeybindDropStack() {
        super("Drop entire stack", Keyboard.KEY_NONE, "Patcher");
    }
}
