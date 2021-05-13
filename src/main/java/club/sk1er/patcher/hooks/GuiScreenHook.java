package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.screen.ResolutionHelper;
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
            ResolutionHelper.setCurrentScaleOverride(desiredScale);
            ResolutionHelper.setScaleOverride(desiredScale);
            final ScaledResolution resolution = new ScaledResolution(mc);
            ResolutionHelper.setScaleOverride(-1);
            return resolution.getScaledWidth();
        }

        return width;
    }

    public static int setWorldAndResolutionHeight(GuiScreen screen, int height) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            final int desiredScale = PatcherConfig.desiredScaleOverride;
            ResolutionHelper.setCurrentScaleOverride(desiredScale);
            ResolutionHelper.setScaleOverride(desiredScale);
            final ScaledResolution resolution = new ScaledResolution(mc);
            ResolutionHelper.setScaleOverride(-1);
            return resolution.getScaledHeight();
        }

        return height;
    }

    public static void handleInputHead(GuiScreen screen) {
        if (mc.thePlayer != null && screen instanceof GuiContainer) {
            ResolutionHelper.setScaleOverride(ResolutionHelper.getCurrentScaleOverride());
        }
    }

    public static void handleInputReturn() {
        ResolutionHelper.setScaleOverride(-1);
    }
}
