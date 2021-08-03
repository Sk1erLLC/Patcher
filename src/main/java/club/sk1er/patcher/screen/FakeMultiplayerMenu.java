package club.sk1er.patcher.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class FakeMultiplayerMenu extends GuiMultiplayer {
    public FakeMultiplayerMenu(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void connectToSelected() {
        this.performDisconnection();
        super.connectToSelected();
    }

    public void performDisconnection() {
        if (this.mc.theWorld != null) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(null);
            this.parentScreen = new GuiMultiplayer(new GuiMainMenu());
        }
    }
}
