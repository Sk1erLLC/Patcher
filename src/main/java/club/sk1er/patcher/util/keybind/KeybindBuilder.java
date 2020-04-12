package club.sk1er.patcher.util.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeybindBuilder {
    public static KeyBinding buildKeybind(String description, int keyCode) {
        KeyBinding keyBinding = new KeyBinding(description, keyCode, "Patcher");
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }
}
