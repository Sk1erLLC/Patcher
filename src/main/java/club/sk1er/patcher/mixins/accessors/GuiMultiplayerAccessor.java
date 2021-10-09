package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiMultiplayer.class)
public interface GuiMultiplayerAccessor {
    @Accessor
    void setParentScreen(GuiScreen screen);

    @Accessor
    void setDirectConnect(boolean b);

    @Accessor
    void setSelectedServer(ServerData data);
}
