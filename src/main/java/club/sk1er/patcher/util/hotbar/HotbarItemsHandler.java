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

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, ItemStack> cachedDamageMap = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat("#.###");
    private boolean renderingArrows;

    /**
     * Create a map for short enchantment name identification.
     * Protection -> P, Fire Protection -> FP, Blast Protection -> BP, etc.
     */
    private final Map<Integer, String> shortEnchantmentNames = new HashMap<Integer, String>() {{
        put(0, "P");
        put(1, "FP");
        put(2, "FF");
        put(3, "BP");
        put(4, "PP");
        put(5, "R");
        put(6, "AA");
        put(7, "T");
        put(8, "DS");
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
    }};

    /**
     * Render the damage of the currently held item above the hotbar for easy viewing.
     *
     * @param event {@link RenderGameOverlayEvent.Post}
     */
    @SubscribeEvent
    public void renderDamage(RenderGameOverlayEvent.Post event) {
        final EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.damageGlance || player == null || player.isSpectator()) {
            return;
        }

        final ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            final ScaledResolution res = event.resolution;

            final String attackDamage = getAttackDamageString(heldItemStack);
            final int x = res.getScaledWidth() - (mc.fontRendererObj.getStringWidth(attackDamage) >> 1);
            final int y = (res.getScaledHeight() - 56 + (mc.playerController.shouldDrawHUD() ? -1 : 14) + 9 << 1) + 9;

            mc.fontRendererObj.drawString(attackDamage, x, y, -1, true);

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
        final EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.itemCountGlance || player == null || player.isSpectator()) {
            return;
        }

        if (player.getCurrentEquippedItem() != null) {
            final boolean holdingBow = player.getCurrentEquippedItem().getItem() instanceof ItemBow;
            final int count = getHeldItemCount(holdingBow);
            final boolean shouldRenderArrowCount = holdingBow && count > 0;
            this.renderingArrows = shouldRenderArrowCount;

            if (count > 1 || shouldRenderArrowCount) {
                final int offset = mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE ? -12 : 0;
                final ScaledResolution resolution = event.resolution;
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
        final EntityPlayerSP player = mc.thePlayer;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.enchantmentsGlance || player == null || player.isSpectator()) {
            return;
        }

        final ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            final String toDraw = heldItemStack.getItem() instanceof ItemPotion ? getPotionEffectString(heldItemStack) : getEnchantmentString(heldItemStack);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            final ScaledResolution res = event.resolution;

            final int x = res.getScaledWidth() - (mc.fontRendererObj.getStringWidth(toDraw) >> 1);
            int y = res.getScaledHeight() - 56 + (mc.playerController.shouldDrawHUD() ? -2 : 14) + 9 << 1;

            if (this.renderingArrows) {
                y -= 10;
            }

            mc.fontRendererObj.drawString(toDraw, x, y, -1, true);

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
            final Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers();
            if (!modifiers.isEmpty()) {
                for (Map.Entry<String, AttributeModifier> entry : modifiers.entries()) {
                    final AttributeModifier modifier = entry.getValue();
                    double damage = modifier.getAmount();

                    if (modifier.getID() == Item.itemModifierUUID) {
                        damage += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                    }

                    final double damageBonus = modifier.getOperation() != 1 && modifier.getOperation() != 2 ? damage : damage * 100.0D;

                    if (damage > 0.0D) {
                        final String target = StatCollector.translateToLocal("attribute.name." + entry.getKey());
                        final String damageString = StatCollector.translateToLocalFormatted(
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

        final ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                final Item item = itemStack.getItem();

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
        final ItemPotion potion = (ItemPotion) heldItemStack.getItem();
        final List<PotionEffect> effects = potion.getEffects(heldItemStack);
        if (effects == null) return "";

        final StringBuilder potionBuilder = new StringBuilder();

        for (PotionEffect entry : effects) {
            final int duration = entry.getDuration() / 20;
            potionBuilder
                .append(EnumChatFormatting.BOLD.toString())
                .append(StatCollector.translateToLocal(entry.getEffectName()))
                .append(" ")
                .append(entry.getAmplifier() + 1)
                .append(" ")
                .append("(")
                .append(duration / 60)
                .append(String.format(":%02d", duration % 60))
                .append(") ");
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
        final Map<Integer, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(heldItemStack);
        final StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : enchantmentMap.entrySet()) {
            sb.append(EnumChatFormatting.BOLD.toString())
                .append(shortEnchantmentNames.get(entry.getKey()))
                .append(" ")
                .append(entry.getValue())
                .append(" ");
        }

        return sb.toString().trim();
    }
}
