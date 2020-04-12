package club.sk1er.patcher.hooks;

import club.sk1er.patcher.tweaker.asm.EnchantmentTransformer;
import net.minecraft.enchantment.Enchantment;
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
}
