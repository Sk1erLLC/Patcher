package club.sk1er.patcher.config;

import gg.essential.vigilance.Vigilant;

import java.io.File;

public class PatcherSoundConfig extends Vigilant {

    public PatcherSoundConfig() {
        // sound config is very different on 1.12, separate 1.8 from 1.12
        super(new File(
            //#if MC==10809
            "./config/patcher_sounds.toml"
            //#else
            //$$ "./config/patcher_sounds-112.toml"
            //#endif
        ));
        initialize();
    }
}
