package club.sk1er.patcher.mixins.bugfixes.modelfixes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSkeleton;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelSkeleton.class)
public class ModelSkeletonMixin_FixedHeldItemTransformations extends ModelBiped {

    //#if MC==10809
    @Override
    public void postRenderArm(float scale) {
        this.bipedRightArm.rotationPointX++;
        this.bipedRightArm.postRender(scale);
        this.bipedRightArm.rotationPointX--;
    }
    //#endif
}
