package club.sk1er.patcher.hooks;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;

public class EnchantmentHook {
  public static String getNumericalName(Enchantment enchantment, int level) {
    return StatCollector.translateToLocal(enchantment.getName()) + " " + level;
  }
}
