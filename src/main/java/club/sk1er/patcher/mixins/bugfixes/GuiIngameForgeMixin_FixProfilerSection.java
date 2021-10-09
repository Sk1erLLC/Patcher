package club.sk1er.patcher.mixins.bugfixes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public abstract class GuiIngameForgeMixin_FixProfilerSection extends GuiIngame {
    public GuiIngameForgeMixin_FixProfilerSection(Minecraft mcIn) {
        super(mcIn);
    }

    @Inject(
        method = "renderChat",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", ordinal = 0, remap = false)),
        at = @At(value = "RETURN", ordinal = 0),
        remap = false
    )
    private void patcher$fixProfilerSectionNotEnding(int width, int height, CallbackInfo ci) {
        if (mc.mcProfiler.getNameOfLastSection().endsWith("chat")) {
            mc.mcProfiler.endSection();
        }
    }
}
