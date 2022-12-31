package club.sk1er.patcher.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.core.ConfigUtils;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.config.elements.OptionSubcategory;
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator;
import cc.polyfrost.oneconfig.gui.elements.config.ConfigSlider;
import com.google.gson.JsonObject;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class PatcherSoundConfig extends Config {
    // sound config is very different on 1.12, separate 1.8 from 1.12
    private transient static final String CONFIG_NAME =
        //#if MC==10809
        "patcher_sounds.json";
        //#else
        //$$ "patcher_sounds-112.json";
        //#endif
    public transient static final Mod soundModImpl = new Mod("Patcher Sounds", ModType.UTIL_QOL, new VigilanceMigrator(CONFIG_NAME));
    public transient final Map<ResourceLocation, BasicOption> data;

    public PatcherSoundConfig(Map<ResourceLocation, BasicOption> data, Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry) {
        super(soundModImpl, CONFIG_NAME);
        mod.config = this;
        this.data = data;
        if (data == null || soundRegistry == null) return;
        initialize(data, soundRegistry);
    }

    public void initialize(Map<ResourceLocation, BasicOption> data, Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry) {
        try {
            for (Map.Entry<ResourceLocation, SoundEventAccessorComposite> entry : soundRegistry.entrySet()) {
                data.computeIfAbsent(entry.getKey(), location -> {
                    String name = getName(location);
                    //#if MC==11202
                    //$$ String[] notCategory = entry.getValue().getLocation().getResourcePath().split("\\.");
                    //#endif
                    String category =
                        //#if MC==10809
                        WordUtils.capitalizeFully(entry.getValue().getSoundCategory().getCategoryName());
                        //#else
                        //$$ WordUtils.capitalizeFully(notCategory[0].replace("_", " "));
                        //#endif
                    String subcategory =
                        //#if MC==10809
                        "Sounds";
                        //#else
                        //$$ notCategory.length > 2 ? WordUtils.capitalizeFully(notCategory[1].replace("_", " ")) : "Sounds";
                        //#endif
                    OptionSubcategory subCategory = ConfigUtils.getSubCategory(mod.defaultPage, category, subcategory);
                    DummySlider slider = new DummySlider(name, category, subcategory);
                    subCategory.options.add(slider);
                    return slider;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean migrate = false;
        File profileFile = ConfigUtils.getProfileFile(configFile);
        if (profileFile.exists()) load();
        if (!profileFile.exists()) {
            if (mod.migrator != null) migrate = true;
            else save();
        }
        mod.config = this;
        generateOptionList(this, mod.defaultPage, mod, migrate);
        if (migrate) save();
        Config.register(mod);
    }

    @Override
    public void save() {
        JsonObject object = new JsonObject();
        for (Map.Entry<ResourceLocation, BasicOption> entry : data.entrySet()) {
            if (entry.getValue() instanceof DummySlider) {
                DummySlider slider = (DummySlider) entry.getValue();
                object.addProperty(slider.name, slider.value);
            }
        }
        ConfigUtils.getProfileFile(configFile).getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(ConfigUtils.getProfileFile(configFile).toPath()), StandardCharsets.UTF_8))) {
            writer.write(gson.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(ConfigUtils.getProfileFile(configFile).toPath()), StandardCharsets.UTF_8))) {
            JsonObject object = gson.fromJson(reader, JsonObject.class);
            for (Map.Entry<ResourceLocation, BasicOption> entry : data.entrySet()) {
                if (entry.getValue() instanceof DummySlider) {
                    DummySlider slider = (DummySlider) entry.getValue();
                    if (object.has(slider.name)) {
                        slider.value = object.get(slider.name).getAsInt();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            File file = ConfigUtils.getProfileFile(configFile);
            file.renameTo(new File(file.getParentFile(), file.getName() + ".corrupted"));
        }
    }

    private String getName(ResourceLocation location) {
        return WordUtils.capitalizeFully(location.getResourcePath().replace(".", " ").replace("_", " "));
    }

    @Override
    public void initialize() {
        // no-op
    }

    private static final class DummySlider extends ConfigSlider {
        private int value = 100;
        private final Field field;
        public DummySlider(String name, String category, String subcategory) {
            super(null, null, name, "Sound Multiplier for " + name,
                category, subcategory, 0, 200, 0);
            setParent(this);
            try {
                field = getClass().getDeclaredField("value");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public Object get() {
            return value;
        }

        @Override
        protected void set(Object object) {
            value = (int) object;
        }
    }
}
