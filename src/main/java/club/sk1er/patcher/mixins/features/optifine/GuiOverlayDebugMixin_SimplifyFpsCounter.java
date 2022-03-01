package club.sk1er.patcher.mixins.features.optifine;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiOverlayDebug.class)
public class GuiOverlayDebugMixin_SimplifyFpsCounter {
    @Dynamic("OptiFine")
    @ModifyArg(method = "call", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuffer;insert(ILjava/lang/String;)Ljava/lang/StringBuffer;", remap = false))
    private String patcher$simplifyFpsCounter(String original) {
        return original.startsWith("/") && PatcherConfig.normalFpsCounter ? "" : original;
    }
}
