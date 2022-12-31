package club.sk1er.patcher.screen.disconnect;

import club.sk1er.patcher.mixins.accessors.GuiMultiplayerAccessor;
import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SmartDisconnectScreen extends GuiScreen {

    private final GuiScreen parentScreen;

    public SmartDisconnectScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, (width >> 1) - 100, (height >> 1), 100, 20, "Disconnect"));
        this.buttonList.add(new GuiButton(1, (width >> 1) + 5, (height >> 1), 100, 20, "Relog"));
        // this will never be centered, and it makes me so upset
        this.buttonList.add(new GuiButton(2, (width >> 1) - 100, (height >> 1) + 24, 206, 20, "Return"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(mc.fontRendererObj, "Would you like to disconnect, or relog?", (width >> 1), (height >> 1) - 24, -1);
        this.drawCenteredString(mc.fontRendererObj, ChatColor.YELLOW + "This can be disabled in Patcher's settings.", (width >> 1), (height >> 1) - 12, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                break;

            case 1:
                // store ip to log in with
                String ip = mc.getCurrentServerData().serverIP;
                String name = mc.getCurrentServerData().serverName;
                GuiMultiplayer multiplayer = new GuiMultiplayer(new GuiMainMenu());

                // disconnect
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(multiplayer);

                // reconnect
                multiplayer.setWorldAndResolution(mc, width, height);
                GuiMultiplayerAccessor accessor = (GuiMultiplayerAccessor) multiplayer;
                accessor.setDirectConnect(true);
                accessor.setSelectedServer(new ServerData(name, ip, false));
                multiplayer.confirmClicked(true, 0);
                break;

            case 2:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }
}
