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

public class KeybindHandler {

    private final KeyBinding dropKeybind = Patcher.instance.getDropKeybind();

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        if (!dropKeybind.isPressed()) {
            return;
        }

        while (dropKeybind.isPressed() && Minecraft.getMinecraft().gameSettings.keyBindDrop.isPressed()) {
            Minecraft.getMinecraft().thePlayer.dropOneItem(true);
        }
    }
}
