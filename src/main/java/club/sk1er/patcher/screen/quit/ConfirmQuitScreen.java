package club.sk1er.patcher.screen.quit;

import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfirmQuitScreen extends GuiScreen {

    private final GuiScreen parentScreen;

    public ConfirmQuitScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, (width / 2) - 100, height / 2, 100, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButton(1, (width / 2) + 5, height / 2, 100, 20, I18n.format("gui.cancel")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(
            mc.fontRendererObj, "Would you like to close the game?",
            width / 2, (height / 2) - 24, -1
        );
        this.drawCenteredString(
            mc.fontRendererObj, ChatColor.YELLOW + "This can be disabled in Patcher's settings.",
            width / 2, (height / 2) - 12, -1
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.shutdown();
        } else if (button.id == 1) {
            mc.displayGuiScreen(this.parentScreen);
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
