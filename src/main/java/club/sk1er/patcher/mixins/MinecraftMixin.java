package club.sk1er.patcher.mixins;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @ModifyArg(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z", remap = false))
    private int replaceF3KeyChecks(int original) {
        if (original == 61)
            return Patcher.instance.getDebugView().getKeyCode();
        return original;
    }
}
