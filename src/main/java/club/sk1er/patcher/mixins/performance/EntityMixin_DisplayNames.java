package club.sk1er.patcher.mixins.performance;

import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin_DisplayNames {
    @Shadow protected abstract HoverEvent getHoverEvent();

    private long patcher$displayNameCachedAt;

    private IChatComponent patcher$cachedDisplayName;

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    protected void patcher$cacheDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        patcher$cachedDisplayName = cir.getReturnValue();
        patcher$displayNameCachedAt = System.currentTimeMillis();
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    protected void patcher$returnCachedDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        if (System.currentTimeMillis() - patcher$displayNameCachedAt < 50L) {
            cir.setReturnValue(patcher$cachedDisplayName);
        }
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getHoverEvent()Lnet/minecraft/event/HoverEvent;"))
    private HoverEvent patcher$doNotGetHoverEvent(Entity instance) {
        // When is a non-player entity going to be sending a chat message?
        return null;
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatStyle;setChatHoverEvent(Lnet/minecraft/event/HoverEvent;)Lnet/minecraft/util/ChatStyle;"))
    private ChatStyle patcher$doNotSetHoverEvent(ChatStyle instance, HoverEvent event) {
        // Let's not set it to null...
        return null;
    }
}
