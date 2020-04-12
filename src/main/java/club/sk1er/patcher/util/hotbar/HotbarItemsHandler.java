package club.sk1er.patcher.util.hotbar;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotbarItemsHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
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

    @SubscribeEvent
    public void renderDamage(RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.damageGlance) {
            return;
        }

        ItemStack heldItemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
        if (heldItemStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            ScaledResolution res = e.resolution;

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

    @SubscribeEvent
    public void renderItemCount(final RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.itemCountGlance) {
            return;
        }

        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            boolean holdingBow = mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow;
            int count = getHeldItemCount(holdingBow);

            if (count > 1 || (holdingBow && count > 0)) {
                int offset = mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE ? 10 : 0;
                ScaledResolution resolution = e.resolution;
                mc.fontRendererObj.drawString(String.valueOf(count),
                    resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.valueOf(count)) >> 1,
                    resolution.getScaledHeight() - 46 - offset,
                    -1,
                    true);
            }
        }
    }

    @SubscribeEvent
    public void renderEnchantments(final RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.TEXT || !PatcherConfig.enchantmentsGlance) {
            return;
        }

        ItemStack heldItemStack = mc.thePlayer.inventory.getCurrentItem();
        if (heldItemStack != null) {
            String toDraw = heldItemStack.getItem() instanceof ItemPotion ? getPotionEffectString(heldItemStack) : getEnchantmentString(heldItemStack);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            ScaledResolution res = e.resolution;

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

    private String getAttackDamageString(ItemStack stack) {
        for (String entry : stack.getTooltip(mc.thePlayer, true)) {
            if (entry.endsWith("Attack Damage")) {
                return entry.split(" ", 2)[0].substring(2);
            }
        }

        return "";
    }

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
