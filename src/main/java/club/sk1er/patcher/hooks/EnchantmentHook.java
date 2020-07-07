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

import club.sk1er.patcher.asm.EnchantmentTransformer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import org.objectweb.asm.tree.ClassNode;

/**
 * Used in {@link EnchantmentTransformer#transform(ClassNode, String)}
 * todo: this doesn't need to be a hook at all
 */
public class EnchantmentHook {
    public static String getNumericalName(Enchantment enchantment, int level) {
        return StatCollector.translateToLocal(enchantment.getName()) + " " + level;
    }
}
