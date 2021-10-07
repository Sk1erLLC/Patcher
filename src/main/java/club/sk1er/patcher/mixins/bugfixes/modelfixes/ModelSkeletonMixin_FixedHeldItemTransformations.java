package club.sk1er.patcher.mixins.bugfixes.modelfixes;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.model.ModelZombie;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelSkeleton.class)
public abstract class ModelSkeletonMixin_FixedHeldItemTransformations extends ModelZombie {

    @Override
    public void postRenderArm(float scale) {
        this.bipedRightArm.rotationPointX++;
        this.bipedRightArm.postRender(scale);
        this.bipedRightArm.rotationPointX--;
    }
}
