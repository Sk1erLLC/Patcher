package club.sk1er.patcher.util.keybind.linux;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;

public class LinuxKeybindFix {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (SystemUtils.IS_OS_LINUX && mc.thePlayer != null && Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            final int eventKey = Keyboard.getEventKey();
            switch (eventKey) {
                case 145:
                    if (mc.gameSettings.keyBindsHotbar[1].getKeyCode() == 3) mc.thePlayer.inventory.currentItem = 1;
                    break;

                case 144:
                    if (mc.gameSettings.keyBindsHotbar[5].getKeyCode() == 7) mc.thePlayer.inventory.currentItem = 5;
                    break;
            }
        }
    }
}
