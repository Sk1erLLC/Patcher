package club.sk1er.patcher.hooks;

import club.sk1er.patcher.tweaker.asm.MinecraftTransformer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link MinecraftTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class MinecraftHook {
    public static void updateKeyBindState() {
        for (KeyBinding keybinding : KeyBinding.keybindArray) {
            try {
                KeyBinding.setKeyBindState(
                    keybinding.getKeyCode(),
                    keybinding.getKeyCode() < 256 && Keyboard.isKeyDown(keybinding.getKeyCode()));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }
}
