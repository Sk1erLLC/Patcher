package club.sk1er.patcher.hooks;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayerHook {

    public static void keyTyped(GuiMultiplayer screen) {
        if (GuiScreen.isCtrlKeyDown()) {
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_1:
                    screen.selectServer(0);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_2:
                    screen.selectServer(1);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_3:
                    screen.selectServer(2);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_4:
                    screen.selectServer(3);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_5:
                    screen.selectServer(4);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_6:
                    screen.selectServer(5);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_7:
                    screen.selectServer(6);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_8:
                    screen.selectServer(7);
                    screen.connectToSelected();
                    break;

                case Keyboard.KEY_9:
                    screen.selectServer(8);
                    screen.connectToSelected();
                    break;

                default:
                    break;
            }
        }
    }
}
