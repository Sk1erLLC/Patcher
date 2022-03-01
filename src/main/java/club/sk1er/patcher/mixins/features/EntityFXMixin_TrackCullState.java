package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.ducks.EntityFXExt;
import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityFX.class)
public class EntityFXMixin_TrackCullState implements EntityFXExt {

    @Unique
    private float patcher$cullState;

    @Override
    public void patcher$setCullState(float cullState) {
        this.patcher$cullState = cullState;
    }

    @Override
    public float patcher$getCullState() {
        return this.patcher$cullState;
    }
}
