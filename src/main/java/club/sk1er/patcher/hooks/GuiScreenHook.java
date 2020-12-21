package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;

@SuppressWarnings("unused")
public class GuiScreenHook {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int setWorldAndResolutionWidth(GuiScreen screen, int width) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            final int desiredScale = PatcherConfig.desiredScaleOverride;
            PatcherConfig.currentScaleOverride = desiredScale;
            PatcherConfig.scaleOverride = desiredScale;
            final ScaledResolution resolution = new ScaledResolution(mc);
            PatcherConfig.scaleOverride = -1;
            return resolution.getScaledWidth();
        }

        return width;
    }

    public static int setWorldAndResolutionHeight(GuiScreen screen, int height) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            final int desiredScale = PatcherConfig.desiredScaleOverride;
            PatcherConfig.currentScaleOverride = desiredScale;
            PatcherConfig.scaleOverride = desiredScale;
            final ScaledResolution resolution = new ScaledResolution(mc);
            PatcherConfig.scaleOverride = -1;
            return resolution.getScaledHeight();
        }

        return height;
    }

    public static void handleInputHead(GuiScreen screen) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            PatcherConfig.scaleOverride = PatcherConfig.currentScaleOverride;
        }
    }

    public static void handleInputReturn() {
        PatcherConfig.scaleOverride = -1;
    }
}
