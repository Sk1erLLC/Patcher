package club.sk1er.patcher.test;

import net.minecraft.client.gui.GuiTextField;

public class BytecodeOutput {

    protected GuiTextField inputField;
    private static boolean supports;
    private static boolean enabled;

    public void fuckkkk() {
        inputField.setMaxStringLength(enabled && supports ? 256 : 100);
    }
}
