package club.sk1er.patcher.mixins.bugfixes;

import cc.polyfrost.oneconfig.libs.universal.USound;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Container.class)
public class ContainerMixin_PlaySound {

    //#if MC==10809
    @Inject(method = "putStackInSlot", at = @At("HEAD"))
    private void patcher$playArmorBreakingSound(int slotID, ItemStack stack, CallbackInfo ci) {
        if (!Minecraft.getMinecraft().theWorld.isRemote || stack != null) {
            return;
        }

        Container container = (Container) (Object) this;
        if (slotID >= 5 && slotID <= 8 && container instanceof ContainerPlayer) {
            Slot slot = container.getSlot(slotID);
            if (slot != null) {
                ItemStack slotStack = slot.getStack();
                if (slotStack != null && slotStack.getItem() instanceof ItemArmor && slotStack.getItemDamage() > slotStack.getMaxDamage() - 2) {
                    USound.INSTANCE.playSoundStatic(new ResourceLocation("random.break"), 1, 1);
                }
            }
        }
    }
    //#endif
}
