package club.sk1er.patcher.screen.disconnect;

import club.sk1er.mods.core.universal.ChatColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SmartDisconnectScreen extends GuiScreen {

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, (width >> 1) - 100, (height >> 1), 100, 20, "Disconnect"));
        this.buttonList.add(new GuiButton(1, (width >> 1) + 5, (height >> 1), 100, 20, "Relog"));
        // this will never be centered and it makes me so upset
        this.buttonList.add(new GuiButton(2, (width >> 1) - 100, (height >> 1) + 24, 206, 20, "Return"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(mc.fontRendererObj, "Would you like to disconnect, or relog?", (width >> 1), (height >> 1) - 24, -1);
        this.drawCenteredString(mc.fontRendererObj, ChatColor.YELLOW + "This can be disabled in Patcher's settings.", (width >> 1), (height >> 1) - 12, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                break;

            case 1:
                // store ip to login with
                final String ip = mc.getCurrentServerData().serverIP;
                final String name = mc.getCurrentServerData().serverName;
                final GuiMultiplayer multiplayer = new GuiMultiplayer(new GuiMainMenu());

                // disconnect
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                this.mc.displayGuiScreen(multiplayer);

                // reconnect
                multiplayer.setWorldAndResolution(mc, width, height);
                multiplayer.directConnect = true;
                multiplayer.selectedServer = new ServerData(name, ip, false);
                multiplayer.confirmClicked(true, 0);
                break;

            case 2:
                this.mc.displayGuiScreen(new GuiIngameMenu());
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(new GuiIngameMenu());
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }
}
