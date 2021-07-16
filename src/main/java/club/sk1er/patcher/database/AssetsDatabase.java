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

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.api.utils.JsonHolder;
import net.minecraft.launchwrapper.Launch;
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
                final File resourceMapFile = new File(dir, "resource_map.txt");
                try (final FileWriter fileWriter = new FileWriter(resourceMapFile)) {
                    final Map<String, String> resourceMap = FallbackResourceManagerHook.resourceMap;
                    final JsonArray array = new JsonArray();

                    for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
                        array.add(new JsonHolder().put("key", entry.getKey()).put("value", entry.getValue()).getObject());
                    }

                    fileWriter.write(array.toString());
                }
            } catch (IOException e) {
                Patcher.instance.getLogger().error("Failed to write to resource map.", e);
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

    public Map<String, String> getAllMap() throws IOException {
        final File resourceMapFile = new File(dir, "resource_map.txt");
        if (!resourceMapFile.exists()) return new HashMap<>();

        Map<String, String> resourceMap = new HashMap<>();
        try {
            for (JsonElement jsonElement : new JsonParser().parse(FileUtils.readFileToString(resourceMapFile)).getAsJsonArray()) {
                final JsonObject asJsonObject = jsonElement.getAsJsonObject();
                resourceMap.put(asJsonObject.get("key").getAsString(), asJsonObject.get("value").getAsString());
            }
        } catch (Exception e) {
            resourceMapFile.delete();
            Patcher.instance.getLogger().error("Failed to read resource map.", e);
        }

        return resourceMap;
    }
}
