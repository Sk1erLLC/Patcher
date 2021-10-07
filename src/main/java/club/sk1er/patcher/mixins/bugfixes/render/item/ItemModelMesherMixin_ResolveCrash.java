package club.sk1er.patcher.mixins.bugfixes.render.item;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemModelMesher.class)
public class ItemModelMesherMixin_ResolveCrash {
    @Shadow @Final private ModelManager modelManager;

    @Inject(method = "getItemModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void patcher$returnMissingModel(ItemStack stack, CallbackInfoReturnable<IBakedModel> cir, Item item) {
        if (item == null) {
            cir.setReturnValue(this.modelManager.getMissingModel());
        }
    }
}
