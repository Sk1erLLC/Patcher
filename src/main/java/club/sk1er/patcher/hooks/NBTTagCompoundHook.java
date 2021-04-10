package club.sk1er.patcher.hooks;

import net.minecraft.nbt.NBTBase;

import java.util.Objects;

@SuppressWarnings("unused")
public class NBTTagCompoundHook {
    public static void checkNullValue(String key, NBTBase value) {
        Objects.requireNonNull(value, "Invalid null NBT value with key " + key);
    }
}
