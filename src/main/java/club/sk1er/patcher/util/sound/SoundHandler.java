/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.sound;

import club.sk1er.patcher.config.ConfigUtil;
import club.sk1er.patcher.config.PatcherConfig;
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
import org.lwjgl.opengl.Display;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SoundHandler implements IResourceManagerReloadListener {

    private final Map<ResourceLocation, PropertyData> data = new HashMap<>();

    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        if (event.result instanceof PositionedSound) {
            if (!Display.isActive()) {
                ((PositionedSound) event.result).volume *= PatcherConfig.unfocusedSounds;
                return;
            }

            ((PositionedSound) event.result).volume *= getVolumeMultiplier(event.result.getSoundLocation());
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
            data.computeIfAbsent(entry.getKey(), location ->
                ConfigUtil.createAndRegisterConfig(PropertyType.SLIDER,
                    WordUtils.capitalizeFully(entry.getValue().getSoundCategory().getCategoryName()),
                    "Sounds",
                    getName(location),
                    "Sound Multiplier for " + getName(location),
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
        return WordUtils.capitalizeFully(location.getResourcePath().replace(".", " ").replace("_", " "));
    }
}
