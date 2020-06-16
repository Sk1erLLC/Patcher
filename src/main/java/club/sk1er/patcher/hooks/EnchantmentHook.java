/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.hooks;

import club.sk1er.patcher.tweaker.asm.EnchantmentTransformer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.DataWatcher;
import net.minecraft.util.StatCollector;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link EnchantmentTransformer#transform(ClassNode, String)}
 */
@SuppressWarnings("unused")
public class EnchantmentHook {
    public static String getNumericalName(Enchantment enchantment, int level) {
        return StatCollector.translateToLocal(enchantment.getName()) + " " + level;
    }

    private byte smallCache = -1;
    private DataWatcher dataWatcher;

    public void setSmall(boolean small) {
        smallCache = (byte) (small ? 1 : 0);
    }

    public boolean isSmall() {
        if (smallCache == -1) {
            smallCache = (byte) ((this.dataWatcher.getWatchableObjectByte(10) & 1) != 0 ? 1  : 0);
        }

        return smallCache > 0;
    }
}
