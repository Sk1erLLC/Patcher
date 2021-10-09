package club.sk1er.patcher.screen.render.title;

import club.sk1er.patcher.mixins.accessors.GuiIngameAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TitleFix {

    @SubscribeEvent
    public void disconnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        GuiIngameAccessor gui = (GuiIngameAccessor) Minecraft.getMinecraft().ingameGUI;

        // these are never cleared when logging out of a server while displaying a title or subtitle,
        // so clear these if they're not already cleared when leaving the server.
        if (!gui.getDisplayedTitle().isEmpty()) gui.setDisplayedTitle("");
        if (!gui.getDisplayedSubTitle().isEmpty()) gui.setDisplayedSubTitle("");
    }
}
