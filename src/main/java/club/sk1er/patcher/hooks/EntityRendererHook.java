package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemMap;

@SuppressWarnings("unused")
public class EntityRendererHook {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean zoomToggled = false;
    private static boolean isBeingHeld = false;
    private static float oldSensitivity;

    public static void fixMissingChunks() {
        mc.renderGlobal.setDisplayListEntitiesDirty();
    }

    public static boolean getZoomState(boolean zoomKeyDown) {
        if (zoomKeyDown) {
            if (isBeingHeld) return zoomToggled;
            isBeingHeld = true;
            zoomToggled = !zoomToggled;
        } else {
            isBeingHeld = false;
        }
        return zoomToggled;
    }

    public static boolean hasMap() {
        return PatcherConfig.mapBobbing && mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemMap;
    }

    public static void reduceSensitivity() {
        oldSensitivity = mc.gameSettings.mouseSensitivity;
        mc.gameSettings.mouseSensitivity = oldSensitivity / PatcherConfig.customZoomSensitivity;
    }

    public static void resetSensitivity() {
        mc.gameSettings.mouseSensitivity = oldSensitivity;
    }
}
