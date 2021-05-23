/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.screen;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.commands.PatcherCommand;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.screen.disconnect.SmartDisconnectScreen;
import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class PatcherMenuEditor {

    private final Minecraft mc = Minecraft.getMinecraft();

    // button ids
    private final int refreshSkin = 435762;
    private final int serverList = 231423;
    private final int allSounds = 85348;

    private List<GuiButton> mcButtonList;
    private GuiButton realmsButton;

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        mcButtonList = event.buttonList;
        final GuiScreen gui = event.gui;
        final int width = gui.width;
        final int height = gui.height;

        if (gui instanceof GuiMainMenu) {
            if (PatcherConfig.cleanMainMenu) {
                realmsButton = ((GuiMainMenu) gui).realmsButton;
                for (GuiButton button : mcButtonList) {
                    if (button.displayString.equals(I18n.format("fml.menu.mods"))) {
                        button.width = 200;
                        break;
                    }
                }
            }
        } else if (gui instanceof GuiScreenResourcePacks) {
            if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                for (GuiButton button : mcButtonList) {
                    button.width = 200;
                    if (button.id == 2) button.xPosition = (width >> 1) - 204;
                }
            }
        } else if (gui instanceof GuiIngameMenu) {
            if (PatcherConfig.skinRefresher) {
                mcButtonList.add(new GuiButton(refreshSkin, 2, height - 22, 100, 20, "Refresh Skin"));
            }

            if (!mc.isSingleplayer() && PatcherConfig.replaceOpenToLan) {
                mcButtonList.get(4).visible = false;
                mcButtonList.get(4).enabled = false;
                mcButtonList.add(new GuiButton(serverList,
                    (width >> 1) - 100, (height / 4) + 56,
                    EssentialAPI.getConfig().getOpenToFriends() ? 98 : 200, 20,
                    "Server List"
                ));
            }
        } else if (gui instanceof GuiCustomizeSkin && mc.theWorld != null) {
            mcButtonList.add(new GuiButton(refreshSkin,
                ((width >> 1) - 155) + 160, (height / 6) + 72,
                150, 20,
                "Refresh Skin"
            ));
        } else if (gui instanceof GuiScreenOptionsSounds) {
            mcButtonList.add(new GuiButton(allSounds, (width >> 1) - 100, (height / 6) + 146, 200, 20, "All Sounds"));
        }
    }

    @SubscribeEvent
    public void preActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiIngameMenu && event.button.displayString.equals(I18n.format("menu.disconnect")) && !mc.isIntegratedServerRunning()) {
            if (PatcherConfig.smartDisconnect) {
                mc.displayGuiScreen(new SmartDisconnectScreen());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        final int id = event.button.id;
        final GuiScreen gui = event.gui;
        if (id == refreshSkin && (gui instanceof GuiIngameMenu || gui instanceof GuiCustomizeSkin)) {
            PatcherCommand.refreshSkin();
        } else if (gui instanceof GuiIngameMenu && id == serverList) {
            mc.displayGuiScreen(new FakeMultiplayerMenu(gui));
        } else if (gui instanceof GuiScreenOptionsSounds && id == allSounds) {
            mc.displayGuiScreen(Patcher.instance.getPatcherSoundConfig().gui());
        }
    }

    @SubscribeEvent
    public void drawMenu(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            if (realmsButton != null) {
                realmsButton.visible = false;
                realmsButton.enabled = false;
            }
        }
    }
}
