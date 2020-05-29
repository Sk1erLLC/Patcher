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

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * Used to handle any and all key presses.
 */
public class KeybindHandler {

    /**
     * Create an instance of our drop entire stack keybind.
     */
    private final KeyBinding dropKeybind = Patcher.instance.getDropKeybind();

    /**
     * Called whenever pressing any key, and processing certain key pressed.
     *
     * @param event {@link InputEvent.KeyInputEvent}
     */
    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (!dropKeybind.isPressed()) {
            return;
        }

        while (dropKeybind.isPressed() && Minecraft.getMinecraft().gameSettings.keyBindDrop.isPressed()) {
            Minecraft.getMinecraft().thePlayer.dropOneItem(true);
        }
    }
}
