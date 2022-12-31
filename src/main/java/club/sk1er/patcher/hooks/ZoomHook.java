package club.sk1er.patcher.hooks;

import cc.polyfrost.oneconfig.libs.elementa.constraints.animation.Animations;
import club.sk1er.patcher.config.PatcherConfig;
import org.lwjgl.input.Mouse;

public class ZoomHook {
    private static final float normalModifier = 4f;
    private static float currentModifier = normalModifier;
    private static boolean hasScrolledYet = false;
    private static long lastMillis = System.currentTimeMillis();
    private static float desiredModifier = currentModifier;

    public static boolean zoomed = false;
    public static float smoothZoomProgress = 0f;

    public static float getScrollZoomModifier() {
        if (!PatcherConfig.scrollToZoom) {
            return normalModifier;
        }
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - lastMillis;
        if (!zoomed) lastMillis = time;

        int moved = Mouse.getDWheel();

        if (moved > 0) {
            smoothZoomProgress = 0f;
            hasScrolledYet = true;
            desiredModifier += 0.25f * desiredModifier;
        } else if (moved < 0) {
            smoothZoomProgress = 0f;
            hasScrolledYet = true;
            desiredModifier -= 0.25f * desiredModifier;
            EntityRendererHook.fixMissingChunks();
        }

        if (desiredModifier < 1f) {
            desiredModifier = 1f;
        }

        if (desiredModifier > 600) {
            desiredModifier = 600f;
        }
        if (PatcherConfig.smoothZoomAnimationWhenScrolling) {
            if (hasScrolledYet && smoothZoomProgress < 1) {
                EntityRendererHook.fixMissingChunks();
                smoothZoomProgress += 0.004F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress > 1 ? 1 : smoothZoomProgress;
                return currentModifier += (desiredModifier - currentModifier) * calculateZoomEasing(smoothZoomProgress);
            }
        } else currentModifier = desiredModifier;
        return desiredModifier;
    }

    public static float getSmoothZoomModifier() {
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - lastMillis;
        lastMillis = time;
        if (zoomed) {
            if (hasScrolledYet) return 1f;
            if (smoothZoomProgress < 1) {
                smoothZoomProgress += 0.005F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress > 1 ? 1 : smoothZoomProgress;
                return 4f - 3f * calculateZoomEasing(smoothZoomProgress);
            }
        } else {
            if (hasScrolledYet) {
                hasScrolledYet = false;
                smoothZoomProgress = 1f;
            }
            if (smoothZoomProgress > 0) {
                smoothZoomProgress -= 0.005F * timeSinceLastChange;
                smoothZoomProgress = smoothZoomProgress < 0 ? 0 : smoothZoomProgress;
                EntityRendererHook.fixMissingChunks();
                float progress = 1 - smoothZoomProgress;
                float diff = PatcherConfig.scrollToZoom ? 1f / currentModifier : 0.25f;
                return diff + (1 - diff) * calculateZoomEasing(progress);
            }
        }
        return 1f;
    }

    private static float calculateZoomEasing(float x) { // todo we should add static methods for our animations too
        switch (PatcherConfig.smoothZoomAlgorithm) {
            case 0:
                return Animations.IN_OUT_QUAD.getValue(x);

            case 1:
                return Animations.IN_OUT_CIRCULAR.getValue(x);

            case 2:
                return Animations.OUT_QUINT.getValue(x);
        }

        // fallback
        return Animations.IN_OUT_QUAD.getValue(x);
    }

    public static void resetZoomState() {
        hasScrolledYet = false;
        currentModifier = normalModifier;
        desiredModifier = normalModifier;
        smoothZoomProgress = 0f;
    }

    public static void handleZoomStateChange(boolean newZoomed) {
        if (newZoomed && !zoomed) {
            Mouse.getDWheel();
        } else if (!newZoomed && zoomed) {
            EntityRendererHook.resetSensitivity();
        }
        zoomed = newZoomed;
    }
}
