package club.sk1er.patcher.mixins.features.network.packet;

import club.sk1er.patcher.asm.render.screen.GuiChatTransformer;
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(C01PacketChatMessage.class)
public class C01PacketChatMessageMixin_ExtendedChatLength {
    @ModifyConstant(method = {"<init>(Ljava/lang/String;)V", "readPacketData"}, constant = @Constant(intValue = 100))
    private int patcher$useExtendedChatLength(int original) {
        return PatcherConfig.extendedChatLength ? GuiChatTransformer.maxChatLength : original;
    }
}
