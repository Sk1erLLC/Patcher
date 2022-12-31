package club.sk1er.patcher.util.world.sound.audioswitcher;

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.config.PatcherConfig;
import cc.polyfrost.oneconfig.utils.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class AudioSwitcher {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ALCHelper alcHelper = new ALCHelper();
    private List<String> devices = new ArrayList<>();

    private boolean previousWasOptionsSounds;
    private boolean changedDevice;
    private int buttonYPosition;

    @SubscribeEvent
    public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        List<GuiButton> buttonList = event.buttonList;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //$$ List<GuiButton> buttonList = event.getButtonList();
        //#endif

        if (gui instanceof GuiScreenOptionsSounds) {
            this.previousWasOptionsSounds = true;
            this.devices = this.alcHelper.getAvailableDevices(true);

            String selectedAudioDevice = PatcherConfig.selectedAudioDevice;
            if (selectedAudioDevice == null || selectedAudioDevice.isEmpty()) {
                selectedAudioDevice = this.devices.isEmpty() ? "Default Sound Device" : this.devices.get(0);
            }

            for (GuiButton button : buttonList) {
                if (button.id == 200) {
                    String buttonText = selectedAudioDevice;
                    int stringWidth = this.mc.fontRendererObj.getStringWidth(buttonText);
                    if (stringWidth >= 175) {
                        buttonText = this.mc.fontRendererObj.trimStringToWidth(buttonText, 170) + "...";
                    }

                    //#if MC==10809
                    this.buttonYPosition = button.yPosition - 44;
                    //#else
                    //$$ this.buttonYPosition = button.y + 60;
                    //#endif
                    buttonList.add(new GuiButton(38732, (gui.width / 2) - 100, this.buttonYPosition, buttonText));
                    break;
                }
            }
        } else if (previousWasOptionsSounds) {
            this.previousWasOptionsSounds = false;
            if (this.changedDevice) {
                Patcher.instance.forceSaveConfig();

                try {
                    this.mc.getSoundHandler().onResourceManagerReload(this.mc.getResourceManager());
                } catch (Exception e) {
                    Notifications.INSTANCE.send("Patcher", "Failed to reinitialize OpenAL.");
                    Patcher.instance.getLogger().error("Failed to reinitialize OpenAL.", e);
                }

                this.changedDevice = false;
            }
        }
    }

    @SubscribeEvent
    public void drawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //#endif

        if (gui instanceof GuiScreenOptionsSounds) {
            gui.drawCenteredString(this.mc.fontRendererObj, "Sound Device (Click to Change)", gui.width / 2, this.buttonYPosition - 12, -1);
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiScreenEvent.ActionPerformedEvent event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        GuiButton button = event.button;
        int buttonId = button.id;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //$$ GuiButton button = event.getButton();
        //$$ int buttonId = button.id;
        //#endif

        if (gui instanceof GuiScreenOptionsSounds && buttonId == 38732) {
            this.fetchAvailableDevicesUncached();
            if (this.devices.isEmpty()) return;

            String selectedAudioDevice = PatcherConfig.selectedAudioDevice;
            if (selectedAudioDevice != null && !selectedAudioDevice.isEmpty()) {
                int index = this.devices.indexOf(selectedAudioDevice);
                if (index + 1 >= this.devices.size()) {
                    selectedAudioDevice = this.devices.get(0);
                } else {
                    selectedAudioDevice = this.devices.get(index + 1);
                }
            } else {
                selectedAudioDevice = this.devices.get(0);
            }

            String buttonText = selectedAudioDevice;
            int stringWidth = this.mc.fontRendererObj.getStringWidth(buttonText);
            if (stringWidth >= 175) {
                buttonText = this.mc.fontRendererObj.trimStringToWidth(buttonText, 170) + "...";
            }

            PatcherConfig.selectedAudioDevice = selectedAudioDevice;
            button.displayString = buttonText;
            if (!this.changedDevice) this.changedDevice = true;
            event.setCanceled(true);
        }
    }

    public void fetchAvailableDevicesUncached() {
        this.devices = this.alcHelper.getAvailableDevices(false);
    }

    public List<String> getDevices() {
        return devices;
    }
}
