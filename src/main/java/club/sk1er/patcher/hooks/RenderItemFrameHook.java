package club.sk1er.patcher.hooks;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.world.entity.culling.EntityCulling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public class RenderItemFrameHook {
    public static boolean shouldRenderItemFrame(Entity entity) {
        if (PatcherConfig.disableItemFrames) {
            return false;
        }

        if (entity instanceof EntityItemFrame) {
            final ItemStack displayedItem = ((EntityItemFrame) entity).getDisplayedItem();
            if (displayedItem != null) {
                final Item item = displayedItem.getItem();
                if (PatcherConfig.disableMappedItemFrames && item != null) {
                    return item != Items.filled_map;
                }
            }
        }

        return !EntityCulling.renderItem(entity);
    }
}
