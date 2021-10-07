package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin_BackgroundRendering {

    @Shadow public Minecraft mc;

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRendering(CallbackInfo ci) {
        if (PatcherConfig.removeContainerBackground && this.mc.theWorld != null) {
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent((GuiScreen) (Object) this));
            ci.cancel();
        }
    }
}
