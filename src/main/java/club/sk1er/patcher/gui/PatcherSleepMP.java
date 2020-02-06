package club.sk1er.patcher.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import java.io.IOException;
import java.util.Objects;

public class PatcherSleepMP extends GuiScreen {

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 40, I18n.format("multiplayer.stopSleeping")));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) wakeFromSleep();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (Objects.requireNonNull(button).id == 0) wakeFromSleep();
        super.actionPerformed(button);
    }

    private void wakeFromSleep() {
        NetHandlerPlayClient netHandlerPlayClient = mc.thePlayer.sendQueue;
        netHandlerPlayClient.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
    }
}
