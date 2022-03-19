package club.sk1er.patcher.util.keybind.linux;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;

public class LinuxKeybindFix {

    private final Minecraft mc = Minecraft.getMinecraft();
    private static final HashMap<Character, Integer> azertyTriggers = new HashMap<Character, Integer>() {{
        put('&', 0);
        put('é', 1);
        put('"', 2);
        put('\'', 3);
        put('(', 4);
        put('§', 5);
        put('è', 6);
        put('!', 7);
        put('ç', 8);
    }};

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (SystemUtils.IS_OS_LINUX && mc.thePlayer != null && Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            if (PatcherConfig.KeyboardLayout == 0) {
                final int eventKey = Keyboard.getEventKey();
                switch (eventKey) {
                    case 145:
                        if (mc.gameSettings.keyBindsHotbar[1].getKeyCode() == 3) mc.thePlayer.inventory.currentItem = 1;
                        break;

                    case 144:
                        if (mc.gameSettings.keyBindsHotbar[5].getKeyCode() == 7) mc.thePlayer.inventory.currentItem = 5;
                        break;
                }
            } else {
                char charPressed = Keyboard.getEventCharacter();
                if (triggers.containsKey(charPressed)) {
                    int i = triggers.get(charPressed);
                    if (mc.gameSettings.keyBindsHotbar[i].getKeyCode() == i + 2) mc.thePlayer.inventory.currentItem = i;
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiPress(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (PatcherConfig.KeyboardLayout == 1 && event.gui instanceof GuiContainer && mc.thePlayer != null && Keyboard.isCreated()
            && Keyboard.getEventKeyState()) {
            char charPressed = Keyboard.getEventCharacter();
            GuiContainer gui = (GuiContainer) event.gui;
            Slot slot = gui.getSlotUnderMouse();
            if (slot != null && triggers.containsKey(charPressed)) {
                mc.playerController.windowClick(
                    gui.inventorySlots.windowId,
                    slot.slotNumber,
                    triggers.get(charPressed),
                    2,
                    event.gui.mc.thePlayer
                );
                event.setCanceled(true);
            }
        }
    }
}
