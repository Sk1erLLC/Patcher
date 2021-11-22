package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.item.ItemMap;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class EntityRendererHook {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean zoomToggled = false;
    private static boolean isBeingHeld = false;
    private static float oldSensitivity;
    private static float partialTicks;
    public static float lastZoomModifier;

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

    public static void reduceSensitivityWhenZoomStarts() {
        oldSensitivity = mc.gameSettings.mouseSensitivity;
        mc.gameSettings.mouseSensitivity = oldSensitivity * PatcherConfig.customZoomSensitivity;
    }

    public static void reduceSensitivityDynamically(float modifier) {
        if (!PatcherConfig.dynamicZoomSensitivity || !ZoomHook.zoomed) return;
        float sensitivity = oldSensitivity * PatcherConfig.customZoomSensitivity;
        sensitivity *= modifier / lastZoomModifier;
        mc.gameSettings.mouseSensitivity = sensitivity;
    }

    public static void resetSensitivity() {
        mc.gameSettings.mouseSensitivity = oldSensitivity;
    }

    public static float getHandFOVModifier(float original) {
        if (PatcherConfig.renderHandWhenZoomed && (ZoomHook.zoomed || (PatcherConfig.smoothZoomAnimation && ZoomHook.smoothZoomProgress > 0))) {
            float f = 70f;
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, mc.thePlayer, partialTicks);

            if (block.getMaterial() == Material.water) {
                f = f * 60.0F / 70.0F;
            }
            return f;
        }
        return original;
    }

    @SubscribeEvent
    public void worldRender(RenderWorldLastEvent event) {
        partialTicks = event.partialTicks;
    }
}
