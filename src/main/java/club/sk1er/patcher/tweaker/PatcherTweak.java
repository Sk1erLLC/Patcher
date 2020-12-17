package club.sk1er.patcher.tweaker;


import club.sk1er.patcher.tweaker.other.ModTweaker;
import net.modcore.loader.ModCoreSetupTweaker;

public class PatcherTweak extends ModCoreSetupTweaker {
    public PatcherTweak() {
        super(new String[]{ModTweaker.class.getName(), PatcherTweaker.class.getName()});
    }
}
