package club.sk1er.patcher.mixins.accessors;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    //#if MC==10809
    @Accessor
    static List<KeyBinding> getKeybindArray() {
        throw new UnsupportedOperationException("Mixin failed to inject!");
    }
    //#endif
}
