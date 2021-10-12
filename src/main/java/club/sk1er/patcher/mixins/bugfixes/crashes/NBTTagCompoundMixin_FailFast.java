package club.sk1er.patcher.mixins.bugfixes.crashes;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(NBTTagCompound.class)
public class NBTTagCompoundMixin_FailFast {

    @Inject(method = "setTag", at = @At("HEAD"))
    private void patcher$failFast(String key, NBTBase value, CallbackInfo ci) {
        Objects.requireNonNull(value, "Invalid null NBT value with key " + key);
    }
}
