package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class RendererLivingEntityHook {

    private static boolean shouldCull = true;

    public static void backFaceCullingStart(Entity entity) {
        shouldCull = PatcherConfig.entityBackFaceCulling && !(entity instanceof EntityPlayer) || PatcherConfig.playerBackFaceCulling && entity instanceof EntityPlayer;

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

    public static float getVisibleHeight(Entity entity) {
        return entity instanceof EntityZombie && ((EntityZombie) entity).isChild() ? entity.height / 2 : entity.height;
    }

    public static boolean shouldRenderNametag(Entity entity1, Entity entity2) {
        if (PatcherConfig.showOwnNametag) return true;
        return entity1 != entity2;
    }
}
