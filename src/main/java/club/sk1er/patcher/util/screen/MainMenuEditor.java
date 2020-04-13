package club.sk1er.patcher.util.screen;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class MainMenuEditor {

    private List<GuiButton> buttonList;

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            buttonList = event.buttonList;
            buttonList.get(3).width = 200;
        }
    }

    @SubscribeEvent
    public void drawMenu(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            buttonList.get(2).visible = false;
            buttonList.get(2).enabled = false;
        }
    }
}
