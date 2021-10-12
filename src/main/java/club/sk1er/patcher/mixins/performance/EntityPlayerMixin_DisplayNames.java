package club.sk1er.patcher.mixins.performance;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin_DisplayNames extends EntityMixin_DisplayNames {
    @Inject(method = "getDisplayName", at = @At("RETURN"))
    private void patcher$cachePlayerDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        super.patcher$cacheDisplayName(cir);
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void patcher$returnCachedPlayerDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        super.patcher$returnCachedDisplayName(cir);
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHoverEvent()Lnet/minecraft/event/HoverEvent;"))
    private HoverEvent patcher$onlyGetHoverEventInSinglePlayer(EntityPlayer instance) {
        // Only needed in single player
        return Minecraft.getMinecraft().isIntegratedServerRunning()
            ? ((EntityPlayerMixin_DisplayNames) (Object) instance).getHoverEvent()
            : null;
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatStyle;setChatHoverEvent(Lnet/minecraft/event/HoverEvent;)Lnet/minecraft/util/ChatStyle;"))
    private ChatStyle patcher$onlySetHoverEventInSinglePlayer(ChatStyle instance, HoverEvent event) {
        return Minecraft.getMinecraft().isIntegratedServerRunning()
            ? instance.setChatHoverEvent(event)
            : null;
    }
}
