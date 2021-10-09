package club.sk1er.patcher.mixins.performance;

import club.sk1er.patcher.mixins.accessors.MinecraftForgeClientAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin_MemoryIssues {
    @Shadow public WorldClient theWorld;

    @Shadow public EntityRenderer entityRenderer;

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void patcher$clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Inject(
        method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER)
    )
    private void patcher$clearRenderCache(CallbackInfo ci) {
        //noinspection ResultOfMethodCallIgnored
        MinecraftForgeClient.getRenderPass(); // Ensure class is loaded, strange accessor issue
        MinecraftForgeClientAccessor.getRegionCache().invalidateAll();
        MinecraftForgeClientAccessor.getRegionCache().cleanUp();
    }
}
