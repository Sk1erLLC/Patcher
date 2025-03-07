package club.sk1er.patcher.screen;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.GuiMainMenuAccessor;
import club.sk1er.patcher.screen.disconnect.SmartDisconnectScreen;
import club.sk1er.patcher.screen.quit.ConfirmQuitScreen;
import gg.essential.api.EssentialAPI;
import gg.essential.api.config.EssentialConfig;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.dsl.ComponentsKt;
import gg.essential.elementa.dsl.UtilitiesKt;
import gg.essential.universal.UMatrixStack;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PatcherMenuEditor {
    private boolean tripped = false;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final int[] sequence = new int[]{
        50, // up
        100, // up
        156, // down
        208, // down
        325, // left
        425, // right
        425, // left
        525, // right
        25, // tab
        72 // return
    };
    private final Window window = (Window) new Window(ElementaVersion.V1).addChild(ComponentsKt.constrain(UIImage.ofResourceCached("/patcher.png"), uiConstraints -> {
        uiConstraints.setX(UtilitiesKt.pixels(0, true));
        uiConstraints.setWidth(UtilitiesKt.pixels(200));
        uiConstraints.setHeight(UtilitiesKt.pixels(200));
        return Unit.INSTANCE;
    }));

    // button ids
    private final int serverList = 231423;
    private final int allSounds = 85348;
    private final int refreshSounds = 85634;

    private List<GuiButton> mcButtonList;
    private GuiButton realmsButton;

    private int next = 0;

    @SubscribeEvent
    public void openMenu(GuiScreenEvent.InitGuiEvent.Post event) {
        //#if MC==10809
        mcButtonList = event.buttonList;
        GuiScreen gui = event.gui;
        //#else
        //$$ mcButtonList = event.getButtonList();
        //$$ GuiScreen gui = event.getGui();
        //#endif
        final int width = gui.width;
        final int height = gui.height;

        if (gui instanceof GuiMainMenu) {
            if (PatcherConfig.cleanMainMenu) {
                realmsButton = ((GuiMainMenuAccessor) gui).getRealmsButton();
                for (GuiButton button : mcButtonList) {
                    if (button.displayString.equals(I18n.format("fml.menu.mods"))) {
                        button.width = 200;
                        break;
                    }
                }
            }
        }
        //#if MC==10809
        else if (gui instanceof GuiOptions && PatcherConfig.cleanOptionsMenu) {
            for (GuiButton button : mcButtonList) {
                if (button.displayString.equals(I18n.format("options.stream"))) {
                    button.visible = false;
                    button.enabled = false;
                } else if (button.displayString.equals(I18n.format("options.sounds"))) {
                    button.xPosition = width / 2 + 5;
                } else if (button.displayString.equals(I18n.format("options.skinCustomisation"))) {
                    button.yPosition = height / 6 + 72 - 6;
                }
            }
        }
        //#endif
        else if (gui instanceof GuiScreenResourcePacks) {
            if (!Loader.isModLoaded("ResourcePackOrganizer")) {
                for (GuiButton button : mcButtonList) {
                    button.width = 200;
                    if (button.id == 2) button.xPosition = (width >> 1) - 204;
                }
            }
        } else if (gui instanceof GuiIngameMenu) {
            if (mc.getCurrentServerData() != null && PatcherConfig.openToLanReplacement > 0) {
                mcButtonList.get(4).visible = false;
                mcButtonList.get(4).enabled = false;
                if (PatcherConfig.openToLanReplacement == 1) {
                    mcButtonList.add(new GuiButton(serverList,
                        (width >> 1) - 100, (height >> 2) + 56,
                        200, 20,
                        "Server List"
                    ));
                }
            }
        } else if (gui instanceof GuiScreenOptionsSounds) {
            //#if MC==10809
            int buttonHeight = height / 6 + 146;
            //#else
            //$$ int buttonHeight = height / 6 + 190;
            //#endif
            mcButtonList.add(new GuiButton(allSounds, (width >> 1) - 100, buttonHeight, 100, 20, "All Sounds"));
            mcButtonList.add(new GuiButton(refreshSounds, (width >> 1), buttonHeight, 100, 20, "Refresh Sounds"));
        }
    }

    @SubscribeEvent
    public void preActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        GuiButton button = event.button;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //$$ GuiButton button = event.getButton();
        //#endif
        if (gui instanceof GuiIngameMenu && button.displayString.equals(I18n.format("menu.disconnect")) && !mc.isIntegratedServerRunning() && PatcherConfig.smartDisconnect) {
            mc.displayGuiScreen(new SmartDisconnectScreen(gui));
            event.setCanceled(true);
        } else if (gui instanceof GuiMainMenu && button.displayString.equals(I18n.format("menu.quit")) && PatcherConfig.confirmQuit) {
            mc.displayGuiScreen(new ConfirmQuitScreen(gui));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        //#if MC==10809
        int buttonId = event.button.id;
        GuiScreen gui = event.gui;
        //#else
        //$$ int buttonId = event.getButton().id;
        //$$ GuiScreen gui = event.getGui();
        //#endif
        if (gui instanceof GuiIngameMenu && buttonId == serverList) {
            mc.displayGuiScreen(new FakeMultiplayerMenu(gui));
        } else if (gui instanceof GuiScreenOptionsSounds) {
            if (buttonId == allSounds) {
                mc.displayGuiScreen(Patcher.instance.getPatcherSoundConfig().gui());
            } else if (buttonId == refreshSounds) {
                mc.getSoundHandler().onResourceManagerReload(mc.getResourceManager());
            }
        }
    }

    @SubscribeEvent
    public void drawMenu(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //#endif
        if (PatcherConfig.cleanMainMenu && gui instanceof GuiMainMenu) {
            if (realmsButton != null) {
                realmsButton.visible = false;
                realmsButton.enabled = false;
            }
        }

        if (PatcherConfig.openToLanReplacement == 2 && gui instanceof GuiIngameMenu) {
            EssentialConfig config = EssentialAPI.getConfig();
            if (config.getOpenToFriends() && config.getEssentialFull() && EssentialAPI.getOnboardingData().hasAcceptedEssentialTOS()) {
                for (GuiButton button : mcButtonList) {
                    if (button != null && button.displayString.equals("Invite Friends")) {
                        button.width = 200;
                        button.xPosition = (gui.width / 2) - 100;
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (tripped && event.phase == TickEvent.Phase.END) {
            window.draw(UMatrixStack.Compat.INSTANCE.get());
        }
    }

    @SubscribeEvent
    public void keyboardInput(GuiScreenEvent.KeyboardInputEvent.Post event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //#endif
        if (gui instanceof GuiMainMenu) {
            int key = Keyboard.getEventKey();
            if (Keyboard.isKeyDown(key) && !Keyboard.isRepeatEvent()) {
                int i = next + 1;
                next = (key >> 3) * ((7 & key) + (i << 1)) == sequence[next] ? i : 0;
                if (next > 9) {
                    next = 0;
                    tripped = !tripped;
                    Patcher.instance.getLogger().info("yep");
                }
            }
        }
    }
}
