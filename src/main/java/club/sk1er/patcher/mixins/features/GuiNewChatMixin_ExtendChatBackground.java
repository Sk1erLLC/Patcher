package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin_ExtendChatBackground {
    //#if MC==10809
    @ModifyArg(
        method = "drawChat", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0),
        index = 0
    )
    private int patcher$extendChatBackgroundLeft(int left) {
        return PatcherConfig.extendChatBackground ? -2 : left;
    }
    //#endif
}
