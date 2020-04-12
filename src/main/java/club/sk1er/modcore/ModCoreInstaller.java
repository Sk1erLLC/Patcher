package club.sk1er.modcore;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/*
    Created by Sk1er for use in all mods. Install under exact package name each time.
 */
public class ModCoreInstaller {


    private static final String VERSION_URL = "https://api.sk1er.club/modcore_versions";
    private static final String className = "club.sk1er.mods.core.ModCore";
    private static boolean errored = false;
    private static String error;
    private static File dataDir = null;
    private static boolean isRunningModCore = false;

    public static boolean isIsRunningModCore() {
        return isRunningModCore;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private static boolean isInitialized() {
        try {
            LinkedHashSet<String> objects = new LinkedHashSet<>();
            objects.add(className);
            Launch.classLoader.clearNegativeEntries(objects);
            Field invalidClasses = LaunchClassLoader.class.getDeclaredField("invalidClasses");
            invalidClasses.setAccessible(true);
            Object obj = invalidClasses.get(ModCoreInstaller.class.getClassLoader());
            ((Set<String>) obj).remove(className);
            return Class.forName("club.sk1er.mods.core.ModCore") != null;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isErrored() {
        return errored;
    }

    public static String getError() {
        return error;
    }

    private static void bail(String error) {
        errored = true;
        ModCoreInstaller.error = error;
    }

    private static JsonHolder readFile(File in) {
        try {
            return new JsonHolder(FileUtils.readFileToString(in, StandardCharsets.UTF_8));
        } catch (IOException ignored) {

        }
        return new JsonHolder();
    }

    public static void initializeModCore(File gameDir) {
        if (!isIsRunningModCore()) {
            return;
        }
        try {
            Class<?> modCore = Class.forName(className);
            Method instanceMethod = modCore.getMethod("getInstance");
            Method initialize = modCore.getMethod("initialize", File.class);
            Object modCoreObject = instanceMethod.invoke(null);
            initialize.invoke(modCoreObject, gameDir);
            System.out.println("Loaded ModCore Successfully");
            return;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println("Did NOT ModCore Successfully");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static int initialize(File gameDir, String minecraftVersion) {
        if (isInitialized()) return -1;
        dataDir = new File(gameDir, "modcore");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                bail("Unable to create necessary files");
                return 1;
            }
        }
        JsonHolder jsonHolder = fetchJSON(VERSION_URL);
        String latestRemote = jsonHolder.optString(minecraftVersion);
        boolean failed = jsonHolder.getKeys().size() == 0 || (jsonHolder.has("success") && !jsonHolder.optBoolean("success"));

        File metadataFile = new File(dataDir, "metadata.json");
        JsonHolder localMetadata = readFile(metadataFile);
        if (failed) latestRemote = localMetadata.optString(minecraftVersion);
        File modcoreFile = new File(dataDir, "Sk1er Modcore-" + latestRemote + " (" + minecraftVersion + ").jar");

        if (!modcoreFile.exists() || !localMetadata.optString(minecraftVersion).equalsIgnoreCase(latestRemote) && !failed) {
            //File does not exist, or is out of date, download it
            File old = new File(dataDir, "Sk1er Modcore-" + localMetadata.optString(minecraftVersion) + " (" + minecraftVersion + ").jar");
            if (old.exists()) old.delete();

            if (!download("https://static.sk1er.club/repo/mods/modcore/" + latestRemote + "/" + minecraftVersion + "/ModCore-" + latestRemote + " (" + minecraftVersion + ").jar", latestRemote, modcoreFile, minecraftVersion, localMetadata)) {
                bail("Unable to download");
                return 2;
            }

        }

        addToClasspath(modcoreFile);

        if (!isInitialized()) {
            bail("Something went wrong and it did not add the jar to the class path. Local file exists? " + modcoreFile.exists());
            return 3;
        }
        isRunningModCore = true;
        return 0;
    }


    public static void addToClasspath(File file) {
        try {
            URL url = file.toURI().toURL();

            ClassLoader classLoader = ModCoreInstaller.class.getClassLoader();
            Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private static boolean download(String url, String version, File file, String mcver, JsonHolder versionData) {
        url = url.replace(" ", "%20");
        System.out.println("Downloading ModCore " + " version " + version + " from: " + url);
        JFrame frame = new JFrame("ModCore Initializer");
        JProgressBar bar = new JProgressBar();
        TextArea comp = new TextArea("", 1, 1, TextArea.SCROLLBARS_NONE);
        frame.getContentPane().add(comp);
        frame.getContentPane().add(bar);
        GridLayout manager = new GridLayout();
        frame.setLayout(manager);
        manager.setColumns(1);
        manager.setRows(2);
        comp.setText("Downloading Sk1er ModCore Library Version " + version + " for Minecraft " + mcver);
        comp.setSize(399, 80);
        comp.setEditable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();


        Dimension preferredSize = new Dimension(400, 225);
        bar.setSize(preferredSize);
        frame.setSize(preferredSize);
        frame.setResizable(false);
        bar.setBorderPainted(true);
        bar.setMinimum(0);
        bar.setStringPainted(true);
        frame.setVisible(true);
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
        Font font = bar.getFont();
        bar.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 4));
        comp.setFont(new Font(font.getName(), font.getStyle(), font.getSize() * 2));

        try {

            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Sk1er Modcore Initializer)");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();
            int contentLength = connection.getContentLength();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            System.out.println("MAX: " + contentLength);
            bar.setMaximum(contentLength);
            int read;
            bar.setValue(0);
            while ((read = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
                bar.setValue(bar.getValue() + 1024);
            }
            outputStream.close();
            FileUtils.write(new File(dataDir, "metadata.json"), versionData.put(mcver, version).toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            frame.dispose();
            return false;
        }
        frame.dispose();
        return true;
    }

    public static JsonHolder fetchJSON(String url) {
        return new JsonHolder(fetchString(url));
    }

    public static String fetchString(String url) {
        url = url.replace(" ", "%20");
        System.out.println("Fetching " + url);
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Sk1er ModCore)");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();
            return IOUtils.toString(is, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed to fetch";
    }


    //Added because we need to use before ModCore is loaded
    static class JsonHolder {
        private JsonObject object;

        public JsonHolder(JsonObject object) {
            this.object = object;
        }

        public JsonHolder(String raw) {
            if (raw == null)
                object = new JsonObject();
            else
                try {
                    this.object = new JsonParser().parse(raw).getAsJsonObject();
                } catch (Exception e) {
                    this.object = new JsonObject();
                    e.printStackTrace();
                }
        }

        public JsonHolder() {
            this(new JsonObject());
        }

        @Override
        public String toString() {
            if (object != null)
                return object.toString();
            return "{}";
        }

        public JsonHolder put(String key, String value) {
            object.addProperty(key, value);
            return this;
        }

        public boolean has(String key) {
            return object.has(key);
        }

        public boolean optBoolean(String key, boolean fallback) {
            try {
                return object.get(key).getAsBoolean();
            } catch (Exception e) {
                return fallback;
            }
        }

        public boolean optBoolean(String key) {
            return optBoolean(key, false);
        }

        public String defaultOptString(String key, String fallBack) {
            try {
                return object.get(key).getAsString();
            } catch (Exception e) {
                return fallBack;
            }
        }

        public String optString(String key) {
            return defaultOptString(key, "");
        }

        public List<String> getKeys() {
            List<String> tmp = new ArrayList<>();
            object.entrySet().forEach(e -> tmp.add(e.getKey()));
            return tmp;
        }

        public void remove(String header) {
            object.remove(header);
        }
    }


}