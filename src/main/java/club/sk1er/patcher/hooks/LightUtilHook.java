package club.sk1er.patcher.hooks;

@SuppressWarnings("unused")
public class LightUtilHook {
    public static float diffuseLight(float x, float y, float z) {
        return Math.min(x * x * 0.6f + y * y * ((3f + y) / 4f) + z * z * 0.8f, 1f);
    }
}
