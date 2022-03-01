package club.sk1er.patcher.mixins.bugfixes;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_PersistentShaders {
    @Redirect(
        //#if MC==10809
        method = "runTick",
        //#else
        //$$ method = "processKeyBinds",
        //#endif
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V")
    )
    private void patcher$keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {
        if (!PatcherConfig.keepShadersOnPerspectiveChange) {
            entityRenderer.loadEntityShader(entityIn);
        }
    }
}
