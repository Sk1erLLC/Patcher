package club.sk1er.patcher.hooks;

import net.minecraft.client.Minecraft;

public class EntityRendererHook {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean zoomToggled = false;
    private static boolean isBeingHeld = false;

    public static void fixMissingChunks() {
        mc.renderGlobal.setDisplayListEntitiesDirty();
    }

    public static boolean getZoomState(boolean zoomKeyDown) {
        if (zoomKeyDown) {
            if (isBeingHeld) return zoomToggled;
            isBeingHeld = true;
            zoomToggled = !zoomToggled;
        }
        else {
            isBeingHeld = false;
        }
        return zoomToggled;
    }
}
