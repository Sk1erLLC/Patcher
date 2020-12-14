/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.tweaker;

import club.sk1er.modcore.ModCoreInstaller;
import club.sk1er.patcher.Patcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class PatcherTweaker implements IFMLLoadingPlugin {

    public static long clientLoadTime;

    public PatcherTweaker() {
        clientLoadTime = System.currentTimeMillis();
        this.createSecondTweaker();
        this.unlockLwjgl();
        this.detectIncompatibleMods();
        System.out.println("POOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOGPOOOOOOOOOG");
    }

    @Override
    public String[] getASMTransformerClass() {
        /*int initialize = ModCoreInstaller.initialize(Launch.minecraftHome, "1.8.9");

        if (ModCoreInstaller.isErrored() || initialize != 0 && initialize != -1) {
            // Technically wouldn't happen in simulated installed but is important for actual impl
            System.out.println("Failed to load Sk1er Modcore - " + initialize + " - " + ModCoreInstaller.getError());
        }

        // If true the classes are loaded
        if (ModCoreInstaller.isIsRunningModCore()) {
            return new String[]{"club.sk1er.mods.core.forge.ClassTransformer", ClassTransformer.class.getName()};
        }*/

        return new String[]{ClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private void createSecondTweaker() {
        try {
            // Create a second internal tweaker, creating after OptiFine does its thing.
            FMLLaunchHandler launchHandler = ReflectionHelper.getPrivateValue(FMLLaunchHandler.class, null, "INSTANCE");
            LaunchClassLoader classLoader = ReflectionHelper.getPrivateValue(FMLLaunchHandler.class, launchHandler, "classLoader");
            Method loadCoreMod = ReflectionHelper.findMethod(CoreModManager.class, null, new String[]{"loadCoreMod"}, LaunchClassLoader.class, String.class, File.class);
            URL path = Patcher.class.getProtectionDomain().getCodeSource().getLocation();
            File mod = new File(path.toURI().getSchemeSpecificPart().split("!")[0]);
            loadCoreMod.invoke(null, classLoader, "club.sk1er.patcher.tweaker.other.ModTweaker", mod);
        } catch (Exception e) {
            System.out.println("Failed creating a second tweaker");
        }
    }

    @SuppressWarnings("unchecked")
    private void unlockLwjgl() {
        boolean lwjglUnlock = false;
        try {
            // Unlock LWJGL, allowing for it to be transformed.
            Field transformerExceptions = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            transformerExceptions.setAccessible(true);
            Object o = transformerExceptions.get(Launch.classLoader);
            lwjglUnlock = ((Set<String>) o).remove("org.lwjgl.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (!lwjglUnlock) {
            System.out.println("Failed to unlock LWJGL, several fixes will not work.");
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void detectIncompatibleMods() {
        File mods = new File(Launch.minecraftHome, "mods");

        if (!mods.exists()) {
            mods.mkdirs(); // make the mods folder for forge if it doesnt exist already
        }

        File[] coreModList = mods.listFiles((dir, name) -> name.endsWith(".jar"));
        for (File file : coreModList) {
            try {
                try (ZipFile zipFile = new ZipFile(file)) {
                    ZipEntry entry = zipFile.getEntry("mcmod.info");

                    if (zipFile.getEntry("io/framesplus/FramesPlus.class") != null) {
                        halt("Patcher is no longer compatible with Frames+ as of 1.3. The Frames+ enhancements have been rewritten for even greater performance and compatibility, and are now included in Patcher.");
                        continue;
                    }

                    if (zipFile.getEntry("club/sk1er/mods/core/ModCore.class") != null) {
                        halt("ModCore should not be in your mods folder. This will cause issues and most likely crash. Please remove it from the mods folder.");
                        continue;
                    }

                    if (entry != null) {
                        try (InputStream inputStream = zipFile.getInputStream(entry)) {
                            byte[] availableBytes = new byte[inputStream.available()];
                            inputStream.read(availableBytes, 0, inputStream.available());
                            JsonObject modInfo = new JsonParser().parse(new String(availableBytes)).getAsJsonArray().get(0).getAsJsonObject();
                            if (!modInfo.has("modid")) {
                                continue;
                            }

                            String modId = modInfo.get("modid").getAsString();
                            if (modId.equals("the5zigMod") && (modInfo.has("url") && !modInfo.get("url").getAsString().equalsIgnoreCase("https://5zigreborn.eu"))) {
                                halt("<html><p>Patcher is not compatible with old 5zig. Please use 5zig reborn found at <a href=\"https://5zigreborn.eu\">https://5zigreborn.eu</a></p></html>");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void halt(final String message) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, message, "Launch Aborted", JOptionPane.ERROR_MESSAGE);
        try {
            final Class<?> aClass = Class.forName("java.lang.Shutdown");
            final Method exit = aClass.getDeclaredMethod("exit", int.class);
            exit.setAccessible(true);
            exit.invoke(null, 0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
