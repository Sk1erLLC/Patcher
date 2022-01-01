package club.sk1er.patcher.util.world.sound;

import club.sk1er.patcher.config.ConfigUtil;
import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.mixins.accessors.PositionedSoundAccessor;
import club.sk1er.patcher.mixins.accessors.SoundHandlerAccessor;
import club.sk1er.patcher.mixins.accessors.SoundRegistryAccessor;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

            //#if MC==11202
            //$$ if (result.getSound() == null) return;
            //#endif
            if (!Display.isActive()) {
                result.setVolume(result.getVolumeField() * PatcherConfig.unfocusedSounds);
            }

            result.setVolume(result.getVolumeField() * getVolumeMultiplier(soundResult.getSoundLocation()));
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
                    return ConfigUtil.createAndRegisterConfig(PropertyType.SLIDER,
                        //#if MC==10809
                        WordUtils.capitalizeFully(entry.getValue().getSoundCategory().getCategoryName()),
                        //#else
                        //$$ "Sound Categories (todo)",
                        //#endif
                        "Sounds", name, "Sound Multiplier for " + name,
                        100, 0, 200, __ -> {
                        }
                    );
                }
            );
        }
    }

    private String getName(ResourceLocation location) {
        return WordUtils.capitalizeFully(location.getResourcePath().replace(".", " ").replace("_", " "));
    }
}
