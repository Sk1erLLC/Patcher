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

package club.sk1er.patcher.util.hotbar;

import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to render stuff above or around the hotbar.
 * <p>
 * Planned to be removed once we start work on a new ChromaHUD version, as this does not fit nor make any
 * sense to be in Patcher, and would fit much better in a centralized HUD mod.
 */
public class HotbarItemsHandler {

    /**
     * Create a Minecraft instance.
     */
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, ItemStack> cachedDamageMap = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat("#.###");

    /**
     * Create a map for short enchantment name identification.
     * Protection -> P, Fire Protection -> FP, Blast Protection -> BP, etc.
     */
    private final Map<Integer, String> shortEnchantmentNames =
        new HashMap<Integer, String>() {
            {
                put(0, "P");
                put(1, "FP");
                put(2, "FF");
                put(3, "BP");
                put(4, "PP");
                put(5, "R");
                put(6, "AA");
                put(7, "T");
                put(8, "DS");
                put(9, "FW");
                put(16, "SH");
                put(17, "SM");
                put(18, "BoA");
                put(19, "KB");
                put(20, "FA");
                put(21, "L");
                put(32, "EFF");
                put(33, "ST");
                put(34, "UNB");
                put(35, "F");
                put(48, "POW");
                put(49, "PUN");
                put(50, "FLA");
                put(51, "INF");
                put(61, "LoS");
                put(62, "LU");
                put(70, "MEN");
            }
        };

    /**
     * Render the damage of the currently held item above the hotbar for easy viewing.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderDamage(RenderGameOverlayEvent.Post event) {
        EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.damageGlance || player == null) {
            return;
        }

        if (player.isSpectator()) {
            return;
        }

        ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            ScaledResolution res = event.resolution;

            String attackDamage = getAttackDamageString(heldItemStack);
            int x = res.getScaledWidth() - (mc.fontRendererObj.getStringWidth(attackDamage) >> 1);
            int y = res.getScaledHeight() - 59;

            y += (mc.playerController.shouldDrawHUD() ? -1 : 14);
            y = y + mc.fontRendererObj.FONT_HEIGHT;
            y <<= 1;
            y += mc.fontRendererObj.FONT_HEIGHT;

            mc.fontRendererObj.drawString(attackDamage, x, y, 13421772, true);

            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Render the item count of the currently held item for easy viewing.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderItemCount(final RenderGameOverlayEvent.Post event) {
        EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.itemCountGlance || player == null) {
            return;
        }

        if (player.isSpectator()) {
            return;
        }

        if (player.getCurrentEquippedItem() != null) {
            boolean holdingBow = player.getCurrentEquippedItem().getItem() instanceof ItemBow;
            int count = getHeldItemCount(holdingBow);

            if (count > 1 || (holdingBow && count > 0)) {
                int offset = mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE ? 10 : 0;
                ScaledResolution resolution = event.resolution;
                mc.fontRendererObj.drawString(String.valueOf(count),
                    resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.valueOf(count)) >> 1,
                    resolution.getScaledHeight() - 46 - offset,
                    -1,
                    true);
            }
        }
    }

    /**
     * Render the enchantments of the currently held item for easy viewing.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderEnchantments(final RenderGameOverlayEvent.Post event) {
        EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.enchantmentsGlance || player == null) {
            return;
        }

        if (player.isSpectator()) {
            return;
        }

        ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            String toDraw = heldItemStack.getItem() instanceof ItemPotion ? getPotionEffectString(heldItemStack) : getEnchantmentString(heldItemStack);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            ScaledResolution res = event.resolution;

            int x = res.getScaledWidth() - (mc.fontRendererObj.getStringWidth(toDraw) >> 1);
            int y = res.getScaledHeight() - 59;

            y += (mc.playerController.shouldDrawHUD() ? -2 : 14);
            y = y + mc.fontRendererObj.FONT_HEIGHT;
            y <<= 1;

            mc.fontRendererObj.drawString(toDraw, x, y, 13421772, true);

            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void clearDamageMap(WorldEvent.Unload event) {
        if (!this.cachedDamageMap.isEmpty()) {
            this.cachedDamageMap.clear();
        }
    }

    /**
     * Get the currently held items attack damage by searching through the items lore.
     *
     * @param stack Currently held item.
     * @return If the item has an "x Attack Damage" string in the lore, return the number, otherwise return empty.
     */
    private String getAttackDamageString(ItemStack stack) {
        if (!this.cachedDamageMap.isEmpty() && this.cachedDamageMap.containsValue(stack)) {
            for (Map.Entry<String, ItemStack> entry : this.cachedDamageMap.entrySet()) {
                if (entry.getValue() == stack) {
                    return entry.getKey();
                }
            }
        }

        if (stack != null) {
            Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers();

            if (!modifiers.isEmpty()) {
                for (Map.Entry<String, AttributeModifier> entry : modifiers.entries()) {
                    AttributeModifier modifier = entry.getValue();
                    double damage = modifier.getAmount();

                    if (modifier.getID() == Item.itemModifierUUID) {
                        damage += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                    }

                    double damageBonus = modifier.getOperation() != 1 && modifier.getOperation() != 2 ? damage : damage * 100.0D;

                    if (damage > 0.0D) {
                        String target = StatCollector.translateToLocal("attribute.name." + entry.getKey());
                        String damageString = StatCollector.translateToLocalFormatted(
                            "attribute.modifier.plus." + modifier.getOperation(),
                            this.format.format(damageBonus),
                            target
                        ).replace(target, "");

                        if (!this.cachedDamageMap.containsKey(damageString) && !this.cachedDamageMap.containsValue(stack)) {
                            this.cachedDamageMap.put(damageString, stack);
                        }

                        return damageString;
                    }
                }
            }
        }

        return "";
    }

    /**
     * Get the amount of the currently held item in the players inventory.
     *
     * @param holdingBow If the player is holding a bow, count arrows instead.
     * @return The amount of the currently held item.
     */
    private int getHeldItemCount(boolean holdingBow) {
        int id = Item.getIdFromItem(mc.thePlayer.getCurrentEquippedItem().getItem());
        int data = mc.thePlayer.getCurrentEquippedItem().getItemDamage();
        int count = 0;

        if (holdingBow) {
            id = Item.getIdFromItem(Items.arrow);
            data = 0;
        }

        ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                Item item = itemStack.getItem();

                if (Item.getIdFromItem(item) == id && itemStack.getItemDamage() == data) {
                    count += itemStack.stackSize;
                }
            }
        }

        return count;
    }

    /**
     * Get the duration and name of the held potion.
     *
     * @param heldItemStack Currently held item.
     * @return Potion duration & name.
     */
    private String getPotionEffectString(ItemStack heldItemStack) {
        ItemPotion potion = (ItemPotion) heldItemStack.getItem();
        List<PotionEffect> effects = potion.getEffects(heldItemStack);
        if (effects == null) return "";

        StringBuilder potionBuilder = new StringBuilder();

        for (PotionEffect entry : effects) {
            int duration = entry.getDuration() / 20;
            potionBuilder.append(EnumChatFormatting.BOLD.toString());
            potionBuilder.append(StatCollector.translateToLocal(entry.getEffectName()));
            potionBuilder.append(" ");
            potionBuilder.append(entry.getAmplifier() + 1);
            potionBuilder.append(" ");
            potionBuilder.append("(");
            potionBuilder.append(duration / 60).append(String.format(":%02d", duration % 60));
            potionBuilder.append(") ");
        }

        return potionBuilder.toString().trim();
    }

    /**
     * Get the enchantments of the currently held item using our short enchantment mapping.
     *
     * @param heldItemStack Currently held item.
     * @return Currently held items enchantments.
     */
    private String getEnchantmentString(ItemStack heldItemStack) {
        String enchantBuilder;
        Map<Integer, Integer> en = EnchantmentHelper.getEnchantments(heldItemStack);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : en.entrySet()) {
            sb.append(EnumChatFormatting.BOLD.toString()).append(shortEnchantmentNames.get(entry.getKey())).append(" ").append(entry.getValue()).append(" ");
        }

        enchantBuilder = sb.toString();
        return enchantBuilder.trim();
    }
}
