package club.sk1er.patcher.mixins.accessors;

//#if MC==11202
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import java.util.Map;
//$$ import java.util.UUID;
//$$ import net.minecraft.client.gui.GuiBossOverlay;
//$$ import net.minecraft.client.gui.BossInfoClient;
//#endif

import org.spongepowered.asm.mixin.Mixin;

//#if MC==10809
import net.minecraft.client.Minecraft;
@Mixin(Minecraft.class) // dummy so it doesn't complain in the mixin file
//#else
//$$ @Mixin(GuiBossOverlay.class)
//#endif
public interface GuiBossOverlayAccessor {
    //#if MC==11202
    //$$ @Accessor
    //$$ Map<UUID, BossInfoClient> getMapBossInfos();
    //#endif
}
