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

package club.sk1er.patcher.tweaker.other;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@SuppressWarnings("unused")
public class ModTweaker implements IFMLLoadingPlugin {

    private void halt(final String message) {
        JOptionPane.showMessageDialog(null, message);
        try {
            final Class<?> aClass = Class.forName("java.lang.Shutdown");
            final Method exit = aClass.getDeclaredMethod("exit", int.class);
            exit.setAccessible(true);
            exit.invoke(null, 0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String[] getASMTransformerClass() {

        final File mods = new File(Launch.minecraftHome, "mods");
        File[] coreModList = mods.listFiles((dir, name) -> name.endsWith(".jar"));
        for (File file : coreModList) {
            try {
                ZipFile zipFile = new ZipFile(file);
                final ZipEntry entry = zipFile.getEntry("mcmod.info");

                if (zipFile.getEntry("io/framesplus/FramesPlus.class") != null) {
                    halt("Patcher is no longer compatible with Frames+ as of 1.3. The Frames+ enhancements have been rewritten for even greater performance and compatibility.");
                    continue;
                }
                if (entry != null) {
                    final InputStream inputStream = zipFile.getInputStream(entry);
                    final byte[] b = new byte[inputStream.available()];
                    inputStream.read(b, 0, inputStream.available());
                    final JsonObject asJsonObject = new JsonParser().parse(new String(b)).getAsJsonArray().get(0).getAsJsonObject();
                    if (!asJsonObject.has("modid")) {
                        continue;
                    }
                    final String modId = asJsonObject.get("modid").getAsString();
                    if (modId.equals("the5zigMod") && (asJsonObject.has("url") && !asJsonObject.get("url").getAsString().equalsIgnoreCase("https://5zigreborn.eu"))) {
                        halt("Patcher is not compatible with old 5zig. Please use 5 zig reborn found at https://5zigreborn.eu");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new String[]{ModClassTransformer.class.getName()};
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
