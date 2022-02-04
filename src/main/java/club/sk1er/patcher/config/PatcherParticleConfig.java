package club.sk1er.patcher.config;

import club.sk1er.patcher.mixins.accessors.EffectRendererAccessor;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.data.ValueBackedPropertyValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PatcherParticleConfig extends Vigilant {
    public Map<Integer, Boolean> map = new HashMap<>();

    public PatcherParticleConfig() {
        super(new File(
            "./config/patcher_particles.toml"
        ));
        initialize();

        EffectRenderer effectRenderer = Minecraft.getMinecraft().effectRenderer;

        Map<Integer, IParticleFactory> particleTypes = ((EffectRendererAccessor)effectRenderer).getParticleTypes();

        for (Map.Entry<Integer, IParticleFactory> entry : particleTypes.entrySet()) {
            String name = EnumParticleTypes.getParticleFromId(entry.getKey()).getParticleName();

            PropertyData data = new PropertyData(
                ConfigUtil.createConfig(
                    PropertyType.SWITCH,
                    "Particles",
                    "General",
                    name,
                    "Toggle visibility of " + name + " particles"
                ),
                new ValueBackedPropertyValue(true),
                this
            );
            registerProperty(data);

            map.put(entry.getKey(), !data.getAsBoolean());
            data.setCallbackConsumer(__ -> map.put(entry.getKey(), data.getAsBoolean()));
        }
    }
}
