package club.sk1er.patcher.tweaker.launch;


import club.sk1er.patcher.tweaker.PatcherTweaker;
import club.sk1er.patcher.tweaker.other.ModTweaker;
import net.modcore.loader.ModCoreSetupTweaker;

public class PatcherTweak extends ModCoreSetupTweaker {

    public static long clientLoadTime;

    public PatcherTweak() {
        super(new String[]{ModTweaker.class.getName(), PatcherTweaker.class.getName()});
        clientLoadTime = System.currentTimeMillis();
    }
}
