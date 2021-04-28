package club.sk1er.patcher.hooks;

import net.minecraft.client.gui.GuiTextField;

public class GuiScreenAddServerHook {
    public static void checkFocusedField(GuiTextField serverNameField, GuiTextField serverIPField) {
        if (!serverNameField.isFocused() && !serverIPField.isFocused()) {
            serverNameField.setFocused(!serverNameField.isFocused());
        } else {
            serverNameField.setFocused(!serverNameField.isFocused());
            serverIPField.setFocused(!serverIPField.isFocused());
        }
    }
}
