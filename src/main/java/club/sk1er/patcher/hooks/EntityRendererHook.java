package club.sk1er.patcher.hooks;

import net.minecraft.client.Minecraft;

public class EntityRendererHook {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void fixMissingChunks() {
        mc.renderGlobal.setDisplayListEntitiesDirty();
    }
}
