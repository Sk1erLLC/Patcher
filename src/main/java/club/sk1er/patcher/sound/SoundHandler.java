package club.sk1er.patcher.sound;

import club.sk1er.patcher.config.ConfigUtil;
import club.sk1er.vigilance.data.PropertyData;
import club.sk1er.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SoundHandler implements IResourceManagerReloadListener {

    private final HashMap<ResourceLocation, PropertyData> data = new HashMap<>();

    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        if (event.result instanceof PositionedSound) {
            PositionedSound result = (PositionedSound) event.result;
            result.volume *= getVolumeMultiplier(event.result.getSoundLocation());
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
        Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry = Minecraft.getMinecraft().getSoundHandler().sndRegistry.soundRegistry;
        for (Entry<ResourceLocation, SoundEventAccessorComposite> entry : soundRegistry.entrySet()) {
            SoundEventAccessorComposite comp = entry.getValue();
            data.computeIfAbsent(entry.getKey(), location ->
                    ConfigUtil.createAndRegisterConfig(PropertyType.SLIDER,
                            "Sounds",
                            WordUtils.capitalizeFully(comp.getSoundCategory().getCategoryName()),
                            getName(location),
                            "Sound Multiplier for " + location.getResourcePath(),
                            100,
                            0,
                            200,
                            o -> {
                            }
                    )
            );
        }
    }

    private String getName(ResourceLocation location) {
        String resourcePath = location.getResourcePath();
        return WordUtils.capitalizeFully(resourcePath.replace(".", " ").replace("_", " "));
    }
}
