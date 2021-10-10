package club.sk1er.patcher.mixins.accessors;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor
    static UUID getItemModifierUUID() {
        throw new UnsupportedOperationException("Mixin failed to inject!");
    }
}
