package club.sk1er.patcher.tweaker;

import club.sk1er.modcore.ModCoreInstaller;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;

import java.io.File;
import java.util.List;

public class LaunchTweaker implements ITweaker {
    private File gameDir = null;

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
            version = ForgeVersion.class.getDeclaredField("mcVersion").get(null) + "_forge";
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        //#else
        //$$ version=net.minecraft.MinecraftVersion.create().getName()+"_fabric";
        //#endif
        // TODO: 12/8/2020 Uncomment for ModCore 2
//        version += "_alpha";
//        int initialize = ModCoreInstaller.initialize(gameDir, version);
//        System.out.println("ModCore Init Status From AutoGG " + initialize);
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
