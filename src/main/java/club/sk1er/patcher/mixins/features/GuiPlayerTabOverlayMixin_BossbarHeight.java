package club.sk1er.patcher.mixins.features;

// todo: make this work with 1.12
//#if MC==10809
import club.sk1er.patcher.config.PatcherConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin_BossbarHeight {

    //#if MC==10809
    @Unique
    private boolean patcher$willShiftDown;

    @Inject(method = "renderPlayerlist", at = @At("HEAD"))
    private void patcher$shiftDownHead(CallbackInfo ci) {
        this.patcher$willShiftDown = PatcherConfig.tabHeightAllow && BossStatus.bossName != null && BossStatus.statusBarTime > 0 && GuiIngameForge.renderBossHealth;
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
    //#endif
}

