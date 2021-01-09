package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class RendererLivingEntityHook {

    private static boolean shouldCull = true;

    public static void backFaceCullingStart(Entity entity) {
        shouldCull = (PatcherConfig.entityBackFaceCulling && !(entity instanceof EntityPlayer)) || (PatcherConfig.playerBackFaceCulling && entity instanceof EntityPlayer);

        if (shouldCull) {
            GlStateManager.enableCull();
        } else {
            GlStateManager.disableCull();
        }
    }

    public static void backFaceCullingEnd() {
        if (shouldCull) {
            GlStateManager.disableCull();
        } else {
            GlStateManager.enableCull();
        }
    }
}
