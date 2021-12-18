package club.sk1er.patcher.mixins.features;

import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

//#if MC==10809
import net.minecraft.entity.boss.BossStatus;
//#else
//$$ import net.minecraft.client.Minecraft;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import club.sk1er.patcher.mixins.accessors.GuiBossOverlayAccessor;
//#endif

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin_BossbarHeight {

    //#if MC==11202
    //$$ @Shadow @Final
    //$$ private Minecraft mc;
    //#endif

    @Unique
    private boolean patcher$willShiftDown;

    @Inject(method = "renderPlayerlist", at = @At("HEAD"))
    private void patcher$shiftDownHead(CallbackInfo ci) {
        //#if MC==10809
        this.patcher$willShiftDown = PatcherConfig.tabHeightAllow && BossStatus.bossName != null && BossStatus.statusBarTime > 0 && GuiIngameForge.renderBossHealth;
        //#else
        //$$ this.patcher$willShiftDown = PatcherConfig.tabHeightAllow && !((GuiBossOverlayAccessor) mc.ingameGUI.getBossOverlay()).getMapBossInfos().isEmpty();
        //#endif
        if (this.patcher$willShiftDown) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, PatcherConfig.tabHeight, 0);
        }
    }

    @Inject(method = "renderPlayerlist", at = @At("TAIL"))
    private void patcher$shiftDownTail(CallbackInfo ci) {
        if (this.patcher$willShiftDown) {
            GlStateManager.popMatrix();
        }
    }
}

