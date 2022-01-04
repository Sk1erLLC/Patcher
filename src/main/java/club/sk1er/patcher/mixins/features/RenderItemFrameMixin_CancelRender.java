package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.util.world.render.culling.EntityCulling;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItemFrame.class)
public class RenderItemFrameMixin_CancelRender {

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItemFrame;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void patcher$cancelRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (PatcherConfig.disableItemFrames) {
            ci.cancel();
        }

        if (entity != null) {
            ItemStack displayedItem = entity.getDisplayedItem();
            if (displayedItem != null) {
                Item item = displayedItem.getItem();
                if (PatcherConfig.disableMappedItemFrames && item != null && item == Items.filled_map) {
                    ci.cancel();
                }
            }
        }

        if (EntityCulling.renderItem(entity)) {
            ci.cancel();
        }
    }
}
