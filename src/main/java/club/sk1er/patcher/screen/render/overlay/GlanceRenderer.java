package club.sk1er.patcher.screen.render.overlay;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.ItemAccessor;
import club.sk1er.patcher.mixins.accessors.ItemStackAccessor;
import com.google.common.collect.Multimap;
import gg.essential.universal.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
//#if MC==10809
import net.minecraft.world.WorldSettings;
//#else
//$$ import net.minecraft.potion.PotionUtils;
//$$ import net.minecraft.world.GameType;
//$$ import net.minecraft.enchantment.Enchantment;
//#endif
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
public class GlanceRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, ItemStack> cachedDamageMap = new HashMap<>();
    private final DecimalFormat format = new DecimalFormat("#.###");
    private boolean renderingArrows;
    private boolean renderingDamage;

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
        EntityPlayerSP player = mc.thePlayer;
        //#if MC==10809
        RenderGameOverlayEvent.ElementType type = event.type;
        ScaledResolution res = event.resolution;
        //#else
        //$$ RenderGameOverlayEvent.ElementType type = event.getType();
        //$$ ScaledResolution res = event.getResolution();
        //#endif
        if (type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.damageGlance || player == null || player.isSpectator()) {
            return;
        }

        final ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            final String attackDamage = getAttackDamageString(heldItemStack);
            if (attackDamage == null) {
                return;
            }

            renderingDamage = true;
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);

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
        EntityPlayerSP player = mc.thePlayer;
        //#if MC==10809
        RenderGameOverlayEvent.ElementType type = event.type;
        ScaledResolution res = event.resolution;
        //#else
        //$$ RenderGameOverlayEvent.ElementType type = event.getType();
        //$$ ScaledResolution res = event.getResolution();
        //#endif
        if (type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.itemCountGlance || player == null || player.isSpectator()) {
            return;
        }

        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null) {
            final boolean holdingBow = currentItem.getItem() instanceof ItemBow;
            final int count = getHeldItemCount(holdingBow);
            final boolean shouldRenderArrowCount = holdingBow && count > 0;
            this.renderingArrows = shouldRenderArrowCount;

            if (count > 1 || shouldRenderArrowCount) {
                final int offset = mc.playerController.getCurrentGameType() ==
                    //#if MC==10809
                    WorldSettings.GameType.CREATIVE
                    //#else
                    //$$ GameType.CREATIVE
                    //#endif
                    ? -12 : 0;
                mc.fontRendererObj.drawString(String.valueOf(count),
                    res.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.valueOf(count)) >> 1,
                    res.getScaledHeight() - 43 - offset,
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
        //#if MC==10809
        RenderGameOverlayEvent.ElementType type = event.type;
        ScaledResolution res = event.resolution;
        //#else
        //$$ RenderGameOverlayEvent.ElementType type = event.getType();
        //$$ ScaledResolution res = event.getResolution();
        //#endif
        if (type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.enchantmentsGlance || player == null || player.isSpectator()) {
            return;
        }

        final ItemStack heldItemStack = player.inventory.getCurrentItem();
        if (heldItemStack != null) {
            final String toDraw = heldItemStack.getItem() instanceof ItemPotion ? getPotionEffectString(heldItemStack) : getEnchantmentString(heldItemStack);
            if (toDraw == null) {
                return;
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);

            final int x = res.getScaledWidth() - (mc.fontRendererObj.getStringWidth(toDraw) >> 1);
            int y = res.getScaledHeight() - 56 + (mc.playerController.shouldDrawHUD() ? 2 : 14) + 9 << 1;

            if (this.renderingArrows || this.renderingDamage) {
                y -= 7;
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
     * Get the currently held items attack damage by searching through the item's lore.
     * todo: make this work in 1.12
     *
     * @param stack Currently held item.
     * @return If the item has an "x Attack Damage" string in the lore, return the number, otherwise return empty.
     */
    private String getAttackDamageString(ItemStack stack) {
        //#if MC==11202
        //$$ return "TODO";
        //#else
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

                    if (modifier.getID() == ItemAccessor.getItemModifierUUID()) {
                        damage += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                    }

                    final double damageBonus = modifier.getOperation() != 1 && modifier.getOperation() != 2 ? damage : damage * 100.0D;

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

        return null;
        //#endif
    }

    /**
     * Get the amount of the currently held item in the players inventory.
     *
     * @param holdingBow If the player is holding a bow, count arrows instead.
     * @return The amount of the currently held item.
     */
    private int getHeldItemCount(boolean holdingBow) {
        ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem == null) return 0;

        int id = Item.getIdFromItem(currentItem.getItem());
        int data = currentItem.getItemDamage();
        int count = 0;

        if (holdingBow) {
            id = Item.getIdFromItem(Items.arrow);
            data = 0;
        }

        for (ItemStack itemStack : mc.thePlayer.inventory.mainInventory) {
            if (itemStack != null) {
                Item item = itemStack.getItem();

                if (Item.getIdFromItem(item) == id && itemStack.getItemDamage() == data) {
                    count += ((ItemStackAccessor) (Object) itemStack).getStackSize();
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
        List<PotionEffect> effects =
            //#if MC==10809
            potion.getEffects(heldItemStack);
            //#else
            //$$ PotionUtils.getEffectsFromStack(heldItemStack);
            //#endif
        if (effects == null) return null;

        StringBuilder potionBuilder = new StringBuilder();

        for (PotionEffect entry : effects) {
            int duration = entry.getDuration() / 20;
            potionBuilder
                .append(ChatColor.BOLD)
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
     * TODO: Make this work with 1.12
     *
     * @param heldItemStack Currently held item.
     * @return Currently held items enchantments.
     */
    private String getEnchantmentString(ItemStack heldItemStack) {
        //#if MC==11202
        //$$ return "TODO";
        //#else
        Map<Integer, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(heldItemStack);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : enchantmentMap.entrySet()) {
            sb.append(ChatColor.BOLD)
                .append(shortEnchantmentNames.get(entry.getKey()))
                .append(" ")
                .append(entry.getValue())
                .append(" ");
        }

        return sb.toString().trim();
        //#endif
    }
}
