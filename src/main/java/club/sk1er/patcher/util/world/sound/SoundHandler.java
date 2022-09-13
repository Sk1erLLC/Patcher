package club.sk1er.patcher.util.world.sound;

import club.sk1er.patcher.config.ConfigUtil;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.PositionedSoundAccessor;
import club.sk1er.patcher.mixins.accessors.SoundHandlerAccessor;
import club.sk1er.patcher.mixins.accessors.SoundManagerAccessor;
import club.sk1er.patcher.mixins.accessors.SoundRegistryAccessor;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.Display;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SoundHandler implements IResourceManagerReloadListener {

    private final Map<ResourceLocation, PropertyData> data = new HashMap<>();

    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        //#if MC==10809
        ISound soundResult = event.result;
        //#else
        //$$ ISound soundResult = event.getResultSound();
        //#endif
        if (soundResult instanceof PositionedSoundAccessor) {
            PositionedSoundAccessor result = (PositionedSoundAccessor) soundResult;

            result.setVolume(result.getVolumeField() * getVolumeMultiplier(soundResult.getSoundLocation()));
        }
    }

    private boolean previousActive = Display.isActive();
    private float previousVolume = 0f;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        boolean active = Display.isActive();
        if (active != previousActive) {
            previousActive = active;
            if (!previousActive) {
                SoundManager soundManager = ((SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler()).getSndManager();
                previousVolume = ((SoundManagerAccessor) soundManager).callGetSoundCategoryVolume(SoundCategory.MASTER);
                if (previousVolume == 0f) return;
                soundManager.setSoundCategoryVolume(SoundCategory.MASTER, PatcherConfig.unfocusedSounds * previousVolume);
            } else {
                ((SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler()).getSndManager().setSoundCategoryVolume(SoundCategory.MASTER, previousVolume);
                previousVolume = 0f;
            }
        }
    }

    private float getVolumeMultiplier(ResourceLocation sound) {
        PropertyData propertyData = data.get(sound);
        if (propertyData != null) {
            Object asAny = propertyData.getAsAny();
            if (asAny instanceof Integer) return ((Integer) asAny).floatValue() / 100F;
        }
        return 1.0f;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry = ((SoundRegistryAccessor) ((SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler()).getSndRegistry()).getSoundRegistry();
        for (Entry<ResourceLocation, SoundEventAccessorComposite> entry : soundRegistry.entrySet()) {
            data.computeIfAbsent(entry.getKey(), location -> {
                    String name = getName(location);
                    //#if MC==10809
                    return ConfigUtil.createAndRegisterConfig(PropertyType.SLIDER,
                        WordUtils.capitalizeFully(entry.getValue().getSoundCategory().getCategoryName()),
                        "Sounds", name, "Sound Multiplier for " + name,
                        100, 0, 200, __ -> {
                        }
                    );
                    //#else
                    //$$ String[] category = entry.getValue().getLocation().getResourcePath().split("\\.");
                    //$$ return ConfigUtil.createAndRegisterConfig(PropertyType.SLIDER,
                    //$$    WordUtils.capitalizeFully(category[0].replace("_", " ")),
                    //$$    category.length > 2 ? WordUtils.capitalizeFully(category[1].replace("_", " ")) : "Sounds", name, "Sound Multiplier for " + name,
                    //$$    100, 0, 200, __ -> {
                    //$$    }
                    //$$ );
                    //#endif
                }
            );
        }
    }

    private String getName(ResourceLocation location) {
        return WordUtils.capitalizeFully(location.getResourcePath().replace(".", " ").replace("_", " "));
    }
}
