package club.sk1er.patcher.tweaker;

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.patcher.Patcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class PatcherTweaker implements IFMLLoadingPlugin {

    public static long clientLoadTime;

    public PatcherTweaker() {
        clientLoadTime = System.currentTimeMillis();

        try {
          FMLLaunchHandler launchHandler = ReflectionHelper.getPrivateValue(FMLLaunchHandler.class, null, "INSTANCE");
            LaunchClassLoader classLoader = ReflectionHelper.getPrivateValue(FMLLaunchHandler.class, launchHandler, "classLoader");
            Method loadCoreMod = ReflectionHelper.findMethod(CoreModManager.class, null, new String[]{"loadCoreMod"}, LaunchClassLoader.class, String.class, File.class);
            URL path = Patcher.class.getProtectionDomain().getCodeSource().getLocation();
            File mod = new File(path.toURI().getSchemeSpecificPart().split("!")[0]);
            loadCoreMod.invoke(null, classLoader, "club.sk1er.patcher.tweaker.optifine.OptifinePatcherTweaker", mod);
        } catch (Exception e) {
            System.out.println("Failed creating a second tweaker");
        }
    }

    /**
     * Return a list of classes that implements the IClassTransformer interface
     *
     * @return a list of classes that implements the IClassTransformer interface
     */
    @Override
    public String[] getASMTransformerClass() {
        int initialize = ModCoreInstaller.initialize(Launch.minecraftHome, "1.8.9");

        if (ModCoreInstaller.isErrored() || initialize != 0 && initialize != -1) {
            // Technically wouldn't happen in simulated installed but is important for actual impl
            System.out.println("Failed to load Sk1er Modcore - " + initialize + " - " + ModCoreInstaller.getError());
        }

        // If true the classes are loaded
        if (ModCoreInstaller.isIsRunningModCore()) {
            return new String[]{"club.sk1er.mods.core.forge.ClassTransformer", ClassTransformer.class.getName()};
        }

        return new String[]{ClassTransformer.class.getName()};
    }

    /**
     * Return a class name that implements "ModContainer" for injection into the mod list The
     * "getName" function should return a name that other mods can, if need be, depend on. Trivially,
     * this modcontainer will be loaded before all regular mod containers, which means it will be
     * forced to be "immutable" - not susceptible to normal sorting behaviour. All other mod
     * behaviours are available however- this container can receive and handle normal loading events
     */
    @Override
    public String getModContainerClass() {
        return null;
    }

    /**
     * Return the class name of an implementor of "IFMLCallHook", that will be run, in the main
     * thread, to perform any additional setup this coremod may require. It will be run
     * <strong>prior</strong> to Minecraft starting, so it CANNOT operate on minecraft itself. The
     * game will deliberately crash if this code is detected to trigger a minecraft class loading
     * (TODO: implement crash ;) )
     */
    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * Inject coremod data into this coremod This data includes: "mcLocation" : the location of the
     * minecraft directory, "coremodList" : the list of coremods "coremodLocation" : the file this
     * coremod loaded from,
     *
     * @param data
     */
    @Override
    public void injectData(Map<String, Object> data) {
    }

    /**
     * Return an optional access transformer class for this coremod. It will be injected post-deobf so
     * ensure your ATs conform to the new srgnames scheme.
     *
     * @return the name of an access transformer class or null if none is provided
     */
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
