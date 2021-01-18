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
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
            case 4:
                this.performDisconnection();
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    public void connectToSelected() {
        this.performDisconnection();
        super.connectToSelected();
    }

    private void performDisconnection() {
        if (this.mc.theWorld != null) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(null);
            this.parentScreen = new GuiMultiplayer(new GuiMainMenu());
        }
    }
}
