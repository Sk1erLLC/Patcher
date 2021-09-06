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

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.asm.client.MinecraftTransformer;
import club.sk1er.patcher.screen.render.overlay.metrics.MetricsData;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link MinecraftTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class MinecraftHook {
    public static MetricsData metricsData;

    public static void updateKeyBindState() {
        for (KeyBinding keybinding : KeyBinding.keybindArray) {
            try {
                final int keyCode = keybinding.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, keyCode < 256 && Keyboard.isKeyDown(keyCode));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }
}
