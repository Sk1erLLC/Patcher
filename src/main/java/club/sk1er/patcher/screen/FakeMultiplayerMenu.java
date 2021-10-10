package club.sk1er.patcher.screen;

import club.sk1er.patcher.mixins.accessors.GuiMultiplayerAccessor;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;

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
            ((GuiMultiplayerAccessor) this).setParentScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }
}
