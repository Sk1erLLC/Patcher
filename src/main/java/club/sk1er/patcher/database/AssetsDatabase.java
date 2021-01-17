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

package club.sk1er.patcher.database;

import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.launchwrapper.Launch;
import net.modcore.api.utils.JsonHolder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class AssetsDatabase {

    private final File dir;

    public AssetsDatabase() {
        File minecraftHome = Launch.minecraftHome;
        if (minecraftHome == null) minecraftHome = new File(".");
        dir = new File(minecraftHome, "patcher");
        dir.mkdir();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveNegative(FallbackResourceManagerHook.negativeResourceCache);
                final File file = new File(dir, "resource_map.txt");
                final FileWriter fileWriter = new FileWriter(file);
                final HashMap<String, String> resourceMap = FallbackResourceManagerHook.resourceMap;
                JsonArray array = new JsonArray();
                for (String s : resourceMap.keySet()) {
                    array.add(new JsonHolder().put("key", s).put("value", resourceMap.get(s)).getObject());
                }
                fileWriter.write(array.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public List<String> getAllNegative() throws IOException {
        File file = new File(dir, "negative_cache.txt");
        return file.exists() ? FileUtils.readLines(file, Charset.defaultCharset()) : new ArrayList<>();
    }

    public void saveNegative(Set<String> lines) throws IOException {
        FileUtils.writeLines(new File(dir, "negative_cache.txt"), lines);
    }

    public Map<String, String> getAllMap() {
        final File file = new File(dir, "resource_map.txt");
        if (!file.exists()) {
            return new HashMap<>();
        }
        HashMap<String, String> map = new HashMap<>();
        try {
            for (JsonElement jsonElement : new JsonParser().parse(FileUtils.readFileToString(file)).getAsJsonArray()) {
                final JsonObject asJsonObject = jsonElement.getAsJsonObject();
                map.put(asJsonObject.get("key").getAsString(), asJsonObject.get("value").getAsString());
            }
        } catch (Exception ignored) {
        }
        return map;
    }
}
