package club.sk1er.patcher.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class ContainerHook {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void playArmorBreakSound(Container container, int slotID, ItemStack stack) {
        if (!mc.theWorld.isRemote || stack != null) {
            return;
        }

        if (slotID >= 5 && slotID <= 8 && container instanceof ContainerPlayer) {
            final Slot slot = container.getSlot(slotID);
            if (slot != null) {
                final ItemStack slotStack = slot.getStack();
                if (slotStack != null && slotStack.getItem() instanceof ItemArmor && slotStack.getItemDamage() > slotStack.getMaxDamage() - 2) {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.break")));
                }
            }
        }
    }
}
