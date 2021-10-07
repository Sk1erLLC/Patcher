package club.sk1er.patcher.mixins.bugfixes.network;

import net.minecraft.util.LazyLoadBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LazyLoadBase.class)
public abstract class LazyLoadBaseMixin_RaceCondition<T> {
    @Shadow private boolean isLoaded;

    @Shadow private T value;

    @Shadow protected abstract T load();

    /**
     * @author LlamaLad7
     * @reason Fix race condition
     */
    @Overwrite
    public T getValue() {
        //noinspection DoubleCheckedLocking
        if (!this.isLoaded) {
            synchronized (this) {
                if (!this.isLoaded) {
                    this.value = this.load();
                    this.isLoaded = true;
                }
            }
        }

        return this.value;
    }
}
