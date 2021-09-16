package club.sk1er.patcher.mixins.modelfixes;

import net.minecraft.client.model.ModelVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ModelVillager.class)
public class ModelVillagerMixin_ResolveRobeHeight {

    @ModifyConstant(method = "<init>(FFII)V", constant = @Constant(intValue = 18))
    private int patcher$changeTextureHeight(int original) {
        return 20;
    }
}
