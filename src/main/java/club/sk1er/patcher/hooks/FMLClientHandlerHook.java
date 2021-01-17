package club.sk1er.patcher.hooks;

import com.google.common.base.CharMatcher;
import net.minecraft.util.StringUtils;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class FMLClientHandlerHook {
    private static final CharMatcher DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf(FontRendererHook.characterDictionary).negate();
    public static String stripSpecialChars(String message) {
        return DISALLOWED_CHAR_MATCHER.removeFrom(StringUtils.stripControlCodes(message));
    }
}
