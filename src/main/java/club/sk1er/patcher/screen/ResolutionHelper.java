package club.sk1er.patcher.screen;

public class ResolutionHelper {
    private static int currentScaleOverride = -1;
    private static int scaleOverride = -1;

    public static int getCurrentScaleOverride() {
        return currentScaleOverride;
    }

    public static void setCurrentScaleOverride(int currentScaleOverride) {
        ResolutionHelper.currentScaleOverride = currentScaleOverride;
    }

    public static int getScaleOverride() {
        return scaleOverride;
    }

    public static void setScaleOverride(int scaleOverride) {
        ResolutionHelper.scaleOverride = scaleOverride;
    }
}
