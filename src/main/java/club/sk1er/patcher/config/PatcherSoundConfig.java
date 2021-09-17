package club.sk1er.patcher.config;

import gg.essential.vigilance.Vigilant;

import java.io.File;

public class PatcherSoundConfig extends Vigilant {

    public PatcherSoundConfig() {
        super(new File("./config/patcher_sounds.toml"));
        initialize();
    }
}
