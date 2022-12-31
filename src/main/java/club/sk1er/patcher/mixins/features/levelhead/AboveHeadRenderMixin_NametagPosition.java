package club.sk1er.patcher.mixins.features.levelhead;

import club.sk1er.patcher.config.PatcherConfig;
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "club.sk1er.mods.levelhead.render.AboveHeadRender")
public class AboveHeadRenderMixin_NametagPosition {

    @Dynamic("Levelhead")
    @Redirect(
        method = "render(Lnet/minecraftforge/client/event/RenderLivingEvent$Specials$Post;)V", remap = false,
        at = @At(value = "INVOKE", target = "Lclub/sk1er/mods/levelhead/render/AboveHeadRender;isSelf(Lnet/minecraft/entity/player/EntityPlayer;)Z", ordinal = 1)
    )
    private boolean patcher$modifyRenderOffset(@Coerce Object aboveHeadRender, EntityPlayer player) {
        return !PatcherConfig.showOwnNametag && UPlayer.getUUID().equals(player.getUniqueID());
    }
}
