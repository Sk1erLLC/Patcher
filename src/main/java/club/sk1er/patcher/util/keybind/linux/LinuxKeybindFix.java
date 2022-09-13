package club.sk1er.patcher.util.keybind.linux;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

//#if MC==11202
//$$ import net.minecraft.inventory.ClickType;
//#endif

public class LinuxKeybindFix {

    private final Minecraft mc = Minecraft.getMinecraft();
    private static final HashMap<Integer, HashMap<Character, Integer>> triggers = new HashMap<Integer, HashMap<Character, Integer>>() {{
        put(1, new HashMap<Character, Integer>() {{ // BE AZERTY
            put('&', 0);
            put('é', 1);
            put('"', 2);
            put('\'', 3);
            put('(', 4);
            put('§', 5);
            put('è', 6);
            put('!', 7);
            put('ç', 8);
        }});
        put(2, new HashMap<Character, Integer>() {{ // FR AZERTY
            put('&', 0);
            put('é', 1);
            put('"', 2);
            put('\'', 3);
            put('(', 4);
            put('-', 5);
            put('è', 6);
            put('_', 7);
            put('ç', 8);
        }});
    }};

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (SystemUtils.IS_OS_LINUX && mc.thePlayer != null && Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            if (PatcherConfig.keyboardLayout == 0) {
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
                if (triggers.get(PatcherConfig.keyboardLayout).containsKey(charPressed)) {
                    int i = triggers.get(PatcherConfig.keyboardLayout).get(charPressed);
                    if (mc.gameSettings.keyBindsHotbar[i].getKeyCode() == i + 2) mc.thePlayer.inventory.currentItem = i;
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiPress(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        //#if MC==10809
        GuiScreen guiScreen = event.gui;
        //#else
        //$$ GuiScreen guiScreen = event.getGui();
        //#endif
        if (SystemUtils.IS_OS_LINUX && PatcherConfig.keyboardLayout != 0 && guiScreen instanceof GuiContainer && mc.thePlayer != null
            && Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            char charPressed = Keyboard.getEventCharacter();
            GuiContainer gui = (GuiContainer) guiScreen;
            Slot slot = gui.getSlotUnderMouse();
            if (slot != null && triggers.get(PatcherConfig.keyboardLayout).containsKey(charPressed)) {
                mc.playerController.windowClick(
                    gui.inventorySlots.windowId,
                    slot.slotNumber,
                    triggers.get(PatcherConfig.keyboardLayout).get(charPressed),
                    //#if MC==10809
                    2,
                    //#else
                    //$$ ClickType.SWAP,
                    //#endif
                    mc.thePlayer
                );
                event.setCanceled(true);
            }
        }
    }
}
