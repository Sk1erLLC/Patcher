package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    //#if MC==10809
    @Accessor
    ItemStack getItemToRender();
    //#endif
}
