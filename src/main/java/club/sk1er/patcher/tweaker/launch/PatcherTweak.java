package club.sk1er.patcher.tweaker.launch;

import club.sk1er.patcher.tweaker.PatcherTweaker;
import club.sk1er.patcher.tweaker.other.ModTweaker;
import gg.essential.loader.EssentialTweaker;

public class PatcherTweak extends EssentialTweaker {

    public static long clientLoadTime;

    public PatcherTweak() {
        super(new String[]{ModTweaker.class.getName(), PatcherTweaker.class.getName()});
        clientLoadTime = System.currentTimeMillis();
    }
}
