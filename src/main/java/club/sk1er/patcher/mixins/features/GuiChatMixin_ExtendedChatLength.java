package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.asm.render.screen.GuiChatTransformer;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiChat.class)
public class GuiChatMixin_ExtendedChatLength {
    //#if MC==10809
    @ModifyConstant(method = "initGui", constant = @Constant(intValue = 100))
    private int patcher$useExtendedChatLength(int original) {
        return PatcherConfig.extendedChatLength ? GuiChatTransformer.maxChatLength : original;
    }
    //#endif
}
