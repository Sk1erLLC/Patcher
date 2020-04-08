package club.sk1er.patcher.keybind;

import club.sk1er.patcher.util.keybind.KeybindBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindDropStack {

  private final KeyBinding dropKey = KeybindBuilder.buildKeybind(
      "Drop Entire Stack",
      Keyboard.KEY_NONE
  );
  private final Minecraft mc = Minecraft.getMinecraft();

  @SubscribeEvent
  public void key(KeyInputEvent event) {
    if (!dropKey.isPressed()) {
      return;
    }

    while (dropKey.isPressed() && mc.gameSettings.keyBindDrop.isPressed()) {
      mc.thePlayer.dropOneItem(true);
    }
  }
}
