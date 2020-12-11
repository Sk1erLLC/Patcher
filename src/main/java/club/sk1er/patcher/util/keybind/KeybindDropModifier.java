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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * Used for dropping entire stacks on computers that don't allow for doing so, such as macOS.
 */
public class KeybindDropModifier extends KeyBinding {
    public KeybindDropModifier() {
        super("Drop Stack Modifier", Keyboard.KEY_NONE, "Patcher");
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null && GameSettings.isKeyDown(this)) {
            player.dropOneItem(true);
        }
    }
}
