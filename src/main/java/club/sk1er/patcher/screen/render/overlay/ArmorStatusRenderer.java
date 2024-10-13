package club.sk1er.patcher.screen.render.overlay;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//#if MC==11202
//$$ import net.minecraft.init.Enchantments;
//#endif

public class ArmorStatusRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderArmor(GuiScreenEvent.DrawScreenEvent.Post event) {
        //#if MC==10809
        GuiScreen gui = event.gui;
        //#else
        //$$ GuiScreen gui = event.getGui();
        //#endif
        if ((PatcherConfig.protectionPercentage || PatcherConfig.projectileProtectionPercentage) && (gui instanceof GuiInventory || gui instanceof GuiContainerCreative)) {
            final String armorValue = getArmorString();
            if (armorValue == null) {
                return;
            }

            final ScaledResolution res = new ScaledResolution(mc);
            mc.fontRendererObj.drawString(armorValue, 10, res.getScaledHeight() - 16, -1, true);
        }
    }

    private String getArmorString() {
        final double protectionPotential = roundDecimals(getArmorPotential(false));
        final double projectileProtectionPotential = roundDecimals(getArmorPotential(true));
        if (protectionPotential == 0.0 && projectileProtectionPotential == 0.0) {
            return null;
        }

        // man don't even try and make me understand what this is doing
        if (!PatcherConfig.protectionPercentage || !PatcherConfig.projectileProtectionPercentage) {
            if (PatcherConfig.protectionPercentage) {
                return protectionPotential + "%";
            }

            if (PatcherConfig.projectileProtectionPercentage) {
                return projectileProtectionPotential + "%";
            }

            return null;
        }

        if (protectionPotential == projectileProtectionPotential) {
            return protectionPotential + "%";
        }

        return protectionPotential + "% | " + projectileProtectionPotential + "%";
    }

    private double roundDecimals(double num) {
        if (num == 0.0) {
            return num;
        }

        num = (int) (num * Math.pow(10.0, 2));
        num /= Math.pow(10.0, 2);
        return num;
    }

    private double getArmorPotential(boolean getProj) {
        double armor = 0;
        int epf = 0;
        int resistance = 0;

        EntityPlayerSP player = mc.thePlayer;
        if (player.isPotionActive(Potion.resistance)) {
            resistance = player.getActivePotionEffect(Potion.resistance).getAmplifier() + 1;
        }

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null) {
                if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armorItem = (ItemArmor) stack.getItem();
                    armor += armorItem.damageReduceAmount * 0.04;
                }

                if (stack.isItemEnchanted()) {
                    epf += getEffProtPoints(EnchantmentHelper.getEnchantmentLevel(
                        //#if MC==10809
                        0,
                        //#else
                        //$$ Enchantments.PROTECTION,
                        //#endif
                        stack), 0.75);
                }

                if (getProj && stack.isItemEnchanted()) {
                    epf += getEffProtPoints(EnchantmentHelper.getEnchantmentLevel(
                        //#if MC==10809
                        4,
                        //#else
                        //$$ Enchantments.PROJECTILE_PROTECTION,
                        //#endif
                        stack), 1.5);
                }
            }
        }

        epf = Math.min(epf, 25);
        double avgDef = addArmorProtResistance(armor, calcProtection(epf), resistance);
        return roundDouble(avgDef * 100.0);
    }

    private int getEffProtPoints(int level, double typeModifier) {
        if (level != 0) {
            return (int) Math.floor((6 + level * level) * typeModifier / 3.0);
        } else {
            return 0;
        }
    }

    private double calcProtection(int armorEpf) {
        double protection = 0.0;

        for (int i = 50; i <= 100; i++) {
            double min = (Math.min(Math.ceil(armorEpf * i / 100.0), 20.0));
            protection += min;
        }

        return protection / 51.0;
    }

    private double addArmorProtResistance(double armor, double prot, int resistance) {
        double protTotal = armor + (1.0 - armor) * prot * 0.04;
        protTotal += (1.0 - protTotal) * resistance * 0.2;
        return Math.min(protTotal, 1.0);
    }

    private double roundDouble(double number) {
        double x = Math.round(number * 10000.0);
        return x / 10000.0;
    }
}
