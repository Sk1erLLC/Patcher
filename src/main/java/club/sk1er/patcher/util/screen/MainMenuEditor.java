package club.sk1er.patcher.util.screen;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class MainMenuEditor {

    private List<GuiButton> buttonList;

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            buttonList = event.buttonList;
            buttonList.get(3).width = 200;
        } else if (event.gui instanceof GuiScreenResourcePacks) {
            if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                for (GuiButton button : event.buttonList) {
                    button.width = 200;

                    if (button.id == 2) {
                        button.xPosition = event.gui.width / 2 - 204;
                    }
                }
            }

            if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                event.buttonList.add(new GuiButton(822462, event.gui.width / 2 + 4, event.gui.height - 24, "Refresh Cache"));
            }
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.button.id == 822462 && event.gui instanceof GuiScreenResourcePacks) {
            FallbackResourceManagerHook.clearCache();
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
