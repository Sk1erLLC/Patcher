package club.sk1er.patcher.mixins.invscale;

import club.sk1er.patcher.screen.ResolutionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

@Mixin(value = ForgeHooksClient.class, remap = false)
public class ForgeHooksClientMixin_InventoryScale {

    @Unique private static final FloatBuffer patcher$projectionMatrixOld = BufferUtils.createFloatBuffer(16);
    @Unique private static final FloatBuffer patcher$modelViewMatrixOld = BufferUtils.createFloatBuffer(16);

    @Inject(method = "drawScreen", at = @At("HEAD"), remap = false)
    private static void patcher$modifyScaleHead(GuiScreen screen, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (Minecraft.getMinecraft().thePlayer != null && screen instanceof GuiContainer) {
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, patcher$projectionMatrixOld);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, patcher$modelViewMatrixOld);

            ResolutionHelper.setScaleOverride(ResolutionHelper.getCurrentScaleOverride());
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"), remap = false)
    private static void patcher$modifyScaleReturn(GuiScreen screen, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (Minecraft.getMinecraft().thePlayer != null && screen instanceof GuiContainer) {
            ResolutionHelper.setScaleOverride(-1);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GL11.glLoadMatrix(patcher$projectionMatrixOld);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadMatrix(patcher$modelViewMatrixOld);
        }
    }

    @ModifyVariable(method = "drawScreen", at = @At("HEAD"), ordinal = 0, remap = false)
    private static int patcher$modifyX(int mouseX) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.currentScreen instanceof GuiContainer) {
            ResolutionHelper.setScaleOverride(ResolutionHelper.getCurrentScaleOverride());
            ScaledResolution resolution = new ScaledResolution(mc);
            mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
        }

        return mouseX;
    }

    @ModifyVariable(method = "drawScreen", at = @At("HEAD"), ordinal = 1, remap = false)
    private static int patcher$modifyY(int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.currentScreen instanceof GuiContainer) {
            ResolutionHelper.setScaleOverride(ResolutionHelper.getCurrentScaleOverride());
            ScaledResolution resolution = new ScaledResolution(mc);
            int height = resolution.getScaledHeight();
            mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
        }

        return mouseY;
    }
}
