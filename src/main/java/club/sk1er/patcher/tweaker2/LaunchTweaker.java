package club.sk1er.patcher.tweaker2;

import club.sk1er.modcore.ModCoreInstaller;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class LaunchTweaker implements ITweaker {
    private File gameDir = null;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.gameDir = gameDir;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // Add our jar as a Mixin container (cause normally Mixin detects those via the TweakClass manifest entry)
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
        //#endif

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
