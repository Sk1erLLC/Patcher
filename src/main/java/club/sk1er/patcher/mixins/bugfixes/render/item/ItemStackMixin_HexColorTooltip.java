package club.sk1er.patcher.mixins.bugfixes.render.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin_HexColorTooltip {
    @Redirect(
        method = "getTooltip",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;toHexString(I)Ljava/lang/String;")
    )
    private String patcher$fixHexColorString(int i) {
        return String.format("%06X", i);
    }
}
