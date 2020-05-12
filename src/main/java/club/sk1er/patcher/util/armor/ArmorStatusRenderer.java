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

package club.sk1er.patcher.util.armor;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorStatusRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderArmor(GuiScreenEvent.DrawScreenEvent e) {
        if ((PatcherConfig.protectionPercentage || PatcherConfig.projectileProtectionPercentage) && (
            e.gui instanceof GuiInventory || e.gui instanceof GuiContainerCreative)) {
            ScaledResolution res = new ScaledResolution(mc);
            String message = getArmorString();
            mc.currentScreen.drawString(mc.fontRendererObj, message, 10, res.getScaledHeight() - 16, -1);
        }
    }

    private String getArmorString() {
        double protectionPotential = roundDecimals(getArmorPotential(false));
        double projectileProtectionPotential = roundDecimals(getArmorPotential(true));
        if (PatcherConfig.protectionPercentage || PatcherConfig.projectileProtectionPercentage) {
            if (PatcherConfig.protectionPercentage) {
                return protectionPotential + "%";
            } else {
                return projectileProtectionPotential + "%";
            }
        }

        if (protectionPotential == projectileProtectionPotential) {
            return protectionPotential + "%";
        } else {
            return protectionPotential + "% | " + projectileProtectionPotential + "%";
        }
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
        EntityPlayer player = mc.thePlayer;
        double armor = 0;
        int epf = 0;
        int resistance = 0;

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
                    epf += getEffProtPoints(EnchantmentHelper.getEnchantmentLevel(0, stack));
                }

                if (getProj && stack.isItemEnchanted()) {
                    epf += getEffProtPoints(EnchantmentHelper.getEnchantmentLevel(4, stack));
                }
            }
        }

        epf = (Math.min(epf, 25));
        double avgDef = addArmorProtResistance(armor, calcProtection(epf), resistance);
        return roundDouble(avgDef * 100.0);
    }

    private int getEffProtPoints(int level) {
        if (level != 0) {
            return (int) Math.floor((6 + level * level) * 0.75 / 3.0);
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
