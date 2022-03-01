package club.sk1er.patcher.mixins.features.optifine;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.hooks.EntityRendererHook;
import club.sk1er.patcher.hooks.ZoomHook;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin_ZoomTweaks {
    @Dynamic("OptiFine")
    @Redirect(method = "getFOVModifier", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void patcher$cancelSmoothCameraAndHandleZoom(GameSettings instance, boolean value) {
        if (!PatcherConfig.normalZoomSensitivity) {
            instance.smoothCamera = value;
        }
        ZoomHook.resetZoomState();
        EntityRendererHook.reduceSensitivityWhenZoomStarts();
    }

    @Dynamic("OptiFine")
    @ModifyConstant(method = "getFOVModifier", constant = @Constant(floatValue = 4f))
    private float patcher$handleScrollZoom(float originalDivisor) {
        return EntityRendererHook.lastZoomModifier = ZoomHook.getScrollZoomModifier();
    }

    @Dynamic("OptiFine")
    @ModifyVariable(method = "getFOVModifier", name = "zoomActive", at = @At(value = "LOAD", ordinal = 0))
    private boolean patcher$handleZoomStateChanged(boolean zoomActive) {
        ZoomHook.handleZoomStateChange(zoomActive);
        return zoomActive;
    }

    @Dynamic("OptiFine")
    @Redirect(method = "getFOVModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;isKeyDown(Lnet/minecraft/client/settings/KeyBinding;)Z"))
    private boolean patcher$handleToggleToZoom(KeyBinding zoomKey) {
        boolean keyDown = GameSettings.isKeyDown(zoomKey);
        if (PatcherConfig.toggleToZoom) {
            return EntityRendererHook.getZoomState(keyDown);
        }
        return keyDown;
    }

    @Dynamic("OptiFine")
    @ModifyVariable(method = "getFOVModifier", name = "f", at = @At(value = "FIELD", target = "Lnet/optifine/reflect/Reflector;ForgeHooksClient_getFOVModifier:Lnet/optifine/reflect/ReflectorMethod;", opcode = Opcodes.GETSTATIC, ordinal = 0, remap = false))
    private float patcher$handleSmoothZoom(float f) {
        float modifier = PatcherConfig.smoothZoomAnimation ? ZoomHook.getSmoothZoomModifier() : 1f;
        EntityRendererHook.reduceSensitivityDynamically(modifier);
        return f * modifier;
    }
}
