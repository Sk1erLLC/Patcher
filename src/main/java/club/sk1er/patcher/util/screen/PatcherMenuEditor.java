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

package club.sk1er.patcher.util.screen;

import club.sk1er.mods.core.ModCore;
import club.sk1er.patcher.command.SkinCacheRefresh;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.List;

public class PatcherMenuEditor {

    private List<GuiButton> mcButtonList;
    private final Minecraft mc = Minecraft.getMinecraft();
    private GuiButton realmsButton;

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        mcButtonList = event.buttonList;

        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            realmsButton = ((GuiMainMenu) event.gui).realmsButton;

            for (GuiButton button : mcButtonList) {
                if (button.displayString.equals(I18n.format("fml.menu.mods"))) {
                    button.width = 200;
                    break;
                }
            }
        } else {
            int width = event.gui.width;
            int height = event.gui.height;

            if (event.gui instanceof GuiScreenResourcePacks) {
                if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                    for (GuiButton button : mcButtonList) {
                        button.width = 200;

                        if (button.id == 2) {
                            button.xPosition = width / 2 - 204;
                        }
                    }
                }
            } else if (event.gui instanceof GuiIngameMenu) {
                if (PatcherConfig.skinRefresher) {
                    mcButtonList.add(new GuiButton(435762,
                        2,
                        height - (ModCore.getInstance().getModCoreConfig().getModcoreButtonLinks() ? 62 : 22),
                        100,
                        20,
                        "Refresh Skin"));
                }

                if (!mc.isSingleplayer() && PatcherConfig.replaceOpenToLan) {
                    mcButtonList.get(4).visible = false;
                    mcButtonList.get(4).enabled = false;
                    mcButtonList.add(new GuiButton(231423, width / 2 - 100, height / 4 + 72 + -16, "Server List"));
                }
            } else if (event.gui instanceof GuiCustomizeSkin && mc.theWorld != null) {
                mcButtonList.add(new GuiButton(435762,
                    width / 2 - 155 + 160,
                    height / 6 + 24 * (7 >> 1),
                    150,
                    20,
                    "Refresh Skin"));
            }
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.button.id == 435762 && (event.gui instanceof GuiIngameMenu || event.gui instanceof GuiCustomizeSkin)) {
            SkinCacheRefresh.refreshSkin();
        } else if (event.gui instanceof GuiIngameMenu && event.button.id == 231423) {
            MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientDisconnectionFromServerEvent(mc.getNetHandler().getNetworkManager()));
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
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
