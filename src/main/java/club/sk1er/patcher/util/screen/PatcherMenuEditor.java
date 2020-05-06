package club.sk1er.patcher.util.screen;

import club.sk1er.mods.core.ModCore;
import club.sk1er.patcher.command.SkinCacheRefresh;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class PatcherMenuEditor {

    private List<GuiButton> buttonList;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        List<GuiButton> mcButtonList = event.buttonList;
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            this.buttonList = mcButtonList;
            this.buttonList.get(3).width = 200;
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

                if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                    mcButtonList.add(new GuiButton(822462, width / 2 + 4, height - 24, "Refresh Cache"));
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
                    mcButtonList.add(new GuiButton(231423, width / 2 - 100, height / 4 + 72 + -16, "Server List") {
                        @Override
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                mc.displayGuiScreen(new ExtendedMultiplayer(event.gui));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
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
        if (event.button.id == 822462 && event.gui instanceof GuiScreenResourcePacks) {
            FallbackResourceManagerHook.clearCache();
        } else if (event.button.id == 435762 && (event.gui instanceof GuiIngameMenu || event.gui instanceof GuiCustomizeSkin)) {
            SkinCacheRefresh.refreshSkin();
        }
    }

    @SubscribeEvent
    public void drawMenu(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (PatcherConfig.cleanMainMenu && event.gui instanceof GuiMainMenu) {
            buttonList.get(2).visible = false;
            buttonList.get(2).enabled = false;
        }
    }

    // taken from canelex
    private static class ExtendedMultiplayer extends GuiMultiplayer {

        public ExtendedMultiplayer(GuiScreen parent) {
            super(parent);
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button.id == 1 || button.id == 4) {
                this.disconnectFromServer();
            }
        }

        @Override
        public void connectToSelected() {
            this.disconnectFromServer();
            super.connectToSelected();
        }

        private void disconnectFromServer() {
            if (mc.theWorld != null) {
                mc.theWorld.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);
                mc.displayGuiScreen(null);
                parentScreen = this;
            }
        }
    }
}
