package club.sk1er.patcher.mixins.performance;

import net.minecraft.nbt.NBTTagString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NBTTagString.class)
public class NBTTagStringMixin_DataCache {

    @Shadow private String data;
    @Unique private String patcher$dataCache;

    @Inject(method = "read", at = @At("HEAD"))
    private void patcher$emptyDataCache(CallbackInfo ci) {
        this.patcher$dataCache = null;
    }

    /**
     * @author asbyth
     * @reason Utilize data cache
     */
    @Overwrite(remap = false)
    public String toString() {
        if (this.patcher$dataCache == null) {
            this.patcher$dataCache = "\"" + this.data.replace("\"", "\\\"") + "\"";
        }

        return this.patcher$dataCache;
    }
}
