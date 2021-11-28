package club.sk1er.patcher.mixins.features;

//#if MC==10809
import club.sk1er.patcher.config.PatcherConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin_NauseaEffect extends AbstractClientPlayer {
    @Shadow public float timeInPortal;

    @Shadow public float prevTimeInPortal;

    public EntityPlayerSPMixin_NauseaEffect(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Override
    public void removePotionEffectClient(int potionId) {
        if (PatcherConfig.nauseaEffect && potionId == Potion.confusion.id) {
            this.timeInPortal = 0.0f;
            this.prevTimeInPortal = 0.0f;
        }

        super.removePotionEffectClient(potionId);
    }
}
//#endif
