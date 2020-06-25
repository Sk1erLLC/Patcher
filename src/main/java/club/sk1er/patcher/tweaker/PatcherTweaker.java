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

import java.io.File;
import java.lang.reflect.Field;
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
import java.util.Set;

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
        boolean lwjglUnlock = false;
        try {
            Field transformerExceptions = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            transformerExceptions.setAccessible(true);
            Object o = transformerExceptions.get(Launch.classLoader);
            lwjglUnlock = ((Set<String>) o).remove("org.lwjgl.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(!lwjglUnlock) {
            System.out.println("Failed to unlock LWJGL. Mouse button 4 fix thing will not work");
        }
    }

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
}
