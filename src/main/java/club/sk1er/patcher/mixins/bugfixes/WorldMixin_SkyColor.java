package club.sk1er.patcher.mixins.bugfixes;

import club.sk1er.patcher.util.world.WorldHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class WorldMixin_SkyColor {
    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getSkyColor(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;", remap = false))
    private Vec3 patcher$staticFogColor(WorldProvider worldProvider, Entity cameraEntity, float partialTicks) {
        return WorldHandler.skyColorVector;
    }
}
