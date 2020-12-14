package club.sk1er.patcher.tweaker2;

import club.sk1er.modcore.ModCoreInstaller;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.CoreModManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class LaunchTweaker implements ITweaker {
    private File gameDir = null;

    public LaunchTweaker() {
        try {
            Field ignored = CoreModManager.class.getDeclaredField("ignoredModFiles");
            ignored.setAccessible(true);
            ((List<String>) ignored.get(null)).remove(new File(gameDir, "/mods/Patcher-LOCAL.jar").getName());

            CoreModManager.getReparseableCoremods().add(new File(gameDir, "/mods/Patcher-LOCAL.jar").getName());

            Method loadCoreMod = CoreModManager.class.getDeclaredMethod("loadCoreMod", LaunchClassLoader.class, String.class, File.class);
            loadCoreMod.setAccessible(true);
            ITweaker tweaker = (ITweaker) loadCoreMod.invoke(null, Launch.classLoader, "club.sk1er.patcher.tweaker.PatcherTweaker", new File(gameDir, "/mods/Patcher-LOCAL.jar"));
            ((List<ITweaker>) Launch.blackboard.get("Tweaks")).add(tweaker);
            tweaker = (ITweaker) loadCoreMod.invoke(null, Launch.classLoader, "club.sk1er.patcher.tweaker.other.ModTweaker", new File(gameDir, "/mods/Patcher-LOCAL.jar"));
            ((List<ITweaker>) Launch.blackboard.get("Tweaks")).add(tweaker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.gameDir = gameDir;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {

        //Minecraft Version
        String version = "unknown";

        //#if FORGE
        try {
            version = "forge_" + ForgeVersion.class.getDeclaredField("mcVersion").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        //#else
        //$$ version=net.minecraft.MinecraftVersion.create().getName()+"_fabric";
        //#endif
        // TODO: 12/8/2020 Uncomment for ModCore 2
//        version += "_alpha";
        int initialize = ModCoreInstaller.initialize(gameDir, version);
        System.out.println("ModCore Init Status From AutoGG " + initialize);

        /*// Add our jar as a Mixin container (cause normally Mixin detects those via the TweakClass manifest entry)
        URI uri;
        try {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        MixinPlatformManager platform = MixinBootstrap.getPlatform();
        //#if MC>=11200
        //$$ platform.addContainer(new ContainerHandleURI(uri));
        //#else
        platform.addContainer(uri);
        //#endif*/
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
