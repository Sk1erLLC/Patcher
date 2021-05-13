package club.sk1er.patcher.hooks;

import club.sk1er.patcher.screen.ResolutionHelper;

@SuppressWarnings("unused")
public class ScaledResolutionHook {
    public static int modifyGuiScale(int guiScale) {
        final int scale = ResolutionHelper.getScaleOverride();
        return scale >= 0 ? scale : guiScale;
    }
}
