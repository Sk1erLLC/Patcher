package club.sk1er.patcher.mixins;

import club.sk1er.patcher.Patcher;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MinecraftMixin_CustomF3 {
    @ModifyArg(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z", remap = false))
    private int patcher$replaceF3KeyChecks(int original) {
        if (original == 61)
            return Patcher.instance.getCustomDebug().getKeyCode();
        return original;
    }
}
