package club.sk1er.patcher.tweaker;

//#if MC==10809
import club.sk1er.patcher.asm.external.forge.ForgeChunkManagerTransformer;
//#endif
import club.sk1er.patcher.asm.external.forge.ModelLoaderTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ASMModParserTransformer;
import club.sk1er.patcher.asm.external.forge.loader.MinecraftForgeTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ModClassLoaderTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ModContainerFactoryTransformer;
import club.sk1er.patcher.asm.external.forge.render.ForgeHooksClientTransformer;
import club.sk1er.patcher.asm.external.forge.render.block.BlockInfoTransformer;
import club.sk1er.patcher.asm.external.forge.render.block.ForgeBlockModelRendererTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.LightUtilTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.VertexLighterFlatTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.VertexLighterSmoothAoTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiIngameForgeTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiModListTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiUtilsTransformer;
import club.sk1er.patcher.asm.external.lwjgl.KeyboardTransformer;
import club.sk1er.patcher.asm.external.lwjgl.LibraryLWJGLOpenALTransformer;
import club.sk1er.patcher.asm.external.lwjgl.WindowsDisplayTransformer;
import club.sk1er.patcher.asm.external.lwjgl.WindowsKeycodesTransformer;
import club.sk1er.patcher.asm.external.mods.optifine.witherfix.EntityWitherTransformer;
import club.sk1er.patcher.asm.network.NetHandlerPlayClientTransformer;
import club.sk1er.patcher.asm.network.NetHandlerPlayServerTransformer;
import club.sk1er.patcher.asm.network.packet.S0EPacketSpawnObjectTransformer;
import club.sk1er.patcher.asm.network.packet.S34PacketMapsTransformer;
import club.sk1er.patcher.asm.render.particle.EffectRendererTransformer;
import club.sk1er.patcher.asm.render.screen.GuiChatTransformer;
import club.sk1er.patcher.asm.render.screen.GuiNewChatTransformer;
import club.sk1er.patcher.asm.render.screen.GuiPlayerTabOverlayTransformer;
import club.sk1er.patcher.asm.render.screen.InventoryEffectRendererTransformer;
import club.sk1er.patcher.asm.render.world.RenderGlobalTransformer;
import club.sk1er.patcher.asm.render.world.VertexFormatTransformer;
import club.sk1er.patcher.asm.render.world.VisGraphTransformer;
import club.sk1er.patcher.asm.render.world.entity.*;
import club.sk1er.patcher.asm.world.entity.data.nbt.NBTTagCompoundTransformer;
import club.sk1er.patcher.optifine.OptiFineGenerations;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import cc.polyfrost.oneconfig.libs.universal.UDesktop;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassTransformer implements IClassTransformer {

    public static final boolean outputBytecode = "true".equals(System.getProperty("patcher.debugBytecode", "false"));
    public static String optifineVersion = "NONE";
    private final Logger logger = LogManager.getLogger("Patcher - Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();

    public static boolean smoothFontDetected;
    public static final Set<String> supportedOptiFineVersions = new HashSet<>();
    public static OptiFineGenerations generations;

    public ClassTransformer() {
        MixinEnvironment.getCurrentEnvironment().addTransformerExclusion(getClass().getName());
        try {
            // detect SmoothFont
            final ClassLoader classLoader = this.getClass().getClassLoader();
            if (classLoader.getResource("bre/smoothfont/mod_SmoothFont.class") != null) {
                smoothFontDetected = true;
                this.logger.warn("SmoothFont detected, disabling FontRenderer optimizations.");
            }

            // OptiFine stuff
            this.fetchSupportedOptiFineVersions();
            this.updateOptiFineGenerations();
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader("Config");
            classReader.accept(classNode, ClassReader.SKIP_CODE);
            for (FieldNode fieldNode : classNode.fields) {
                if (fieldNode.name.equals("OF_RELEASE")) {
                    optifineVersion = (String) fieldNode.value;
                    break;
                }
            }

            if (!supportedOptiFineVersions.contains(optifineVersion)) {
                logger.info("User has outdated OptiFine. (version: OptiFine-{})", optifineVersion);
                this.haltForOptiFine("Patcher has detected OptiFine " + optifineVersion + ", which is not supported and will crash.\n" +
                    "Please update to a supported version of OptiFine and try again.\n" +
                    "Supported versions: " + StringUtils.join(supportedOptiFineVersions, ", "));
                return;
            }
        } catch (IOException ignored) {
        }

        //#if MC==10809
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new S0EPacketSpawnObjectTransformer());
        registerTransformer(new RenderXPOrbTransformer());
        registerTransformer(new RenderFireballTransformer());
        registerTransformer(new RenderFishTransformer());
        registerTransformer(new RenderSnowballTransformer());
        //#endif
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new GuiUtilsTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new LayerCustomHeadTransformer());
        registerTransformer(new NBTTagCompoundTransformer());
        registerTransformer(new GuiChatTransformer());
        registerTransformer(new VisGraphTransformer());
        registerTransformer(new EffectRendererTransformer());
        registerTransformer(new VertexFormatTransformer());
        registerTransformer(new LayerHeldItemTransformer());
        registerTransformer(new NetHandlerPlayServerTransformer());
        registerTransformer(new EntityWitherTransformer());
        registerTransformer(new S34PacketMapsTransformer());
        registerTransformer(new RenderWitherTransformer());
        registerTransformer(new LibraryLWJGLOpenALTransformer());
        if (isDevelopment()) registerTransformer(new InventoryEffectRendererTransformer());

        // forge classes
        //#if MC==10809
        registerTransformer(new ForgeHooksClientTransformer());
        registerTransformer(new GuiModListTransformer());
        registerTransformer(new ModClassLoaderTransformer());
        registerTransformer(new ModelLoaderTransformer());
        registerTransformer(new ForgeChunkManagerTransformer());
        registerTransformer(new BlockInfoTransformer());
        registerTransformer(new VertexLighterFlatTransformer());
        registerTransformer(new VertexLighterSmoothAoTransformer());
        registerTransformer(new ForgeBlockModelRendererTransformer());
        registerTransformer(new MinecraftForgeTransformer());
        registerTransformer(new ASMModParserTransformer());
        registerTransformer(new LightUtilTransformer());
        registerTransformer(new ModContainerFactoryTransformer());
        //#endif
        registerTransformer(new GuiIngameForgeTransformer());
        //registerTransformer(new JarDiscovererTransformer());

        // lwjgl
        registerTransformer(new WindowsDisplayTransformer());
        registerTransformer(new WindowsKeycodesTransformer());
        registerTransformer(new KeyboardTransformer());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] createTransformer(String transformedName, byte[] bytes, Multimap<String, PatcherTransformer> transformerMap, Logger logger) {
        if (bytes == null) return null;

        Collection<PatcherTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (PatcherTransformer transformer : transformers) {
            transformer.transform(classNode, transformedName);
        }

        PatcherClassWriter classWriter = new PatcherClassWriter(PatcherClassWriter.COMPUTE_FRAMES);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            logger.error("Exception when transforming {} : {}", transformedName, e.getClass().getSimpleName(), e);
        }

        if (outputBytecode) {
            File bytecodeDirectory = new File("bytecode");
            if (!bytecodeDirectory.exists()) bytecodeDirectory.mkdirs();

            int lastIndex = transformedName.lastIndexOf('.');
            if (lastIndex != -1) {
                transformedName = transformedName.substring(lastIndex + 1) + ".class";
            }

            try {
                File bytecodeOutput = new File(bytecodeDirectory, transformedName);
                if (!bytecodeOutput.exists()) bytecodeOutput.createNewFile();

                try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                    os.write(classWriter.toByteArray());
                } catch (IOException e) {
                    logger.error("Failed to create bytecode output for {}.", transformedName, e);
                }
            } catch (Exception ignored) {
            }
        }

        return classWriter.toByteArray();
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return createTransformer(transformedName, bytes, transformerMap, logger);
    }

    private void haltForOptiFine(String message) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JButton openOptifine = new JButton("Open OptiFine Website");
        openOptifine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    UDesktop.browse(new URI("https://optifine.net/downloads/"));
                } catch (Exception ex) {
                    JLabel label = new JLabel();
                    label.setText("Failed to open OptiFine website.");
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    label.setAlignmentY(Component.CENTER_ALIGNMENT);
                }
            }
        });

        JButton close = new JButton("Close");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PatcherTweaker.invokeExit();
            }
        });

        Object[] options = {openOptifine, close};
        JOptionPane.showOptionDialog(frame, message, "Launch Aborted", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        PatcherTweaker.invokeExit();
    }

    private void fetchSupportedOptiFineVersions() {
        HttpsURLConnection connection = null;
        try {
            URL optifineVersions = new URL("https://static.sk1er.club/patcher/optifine.txt");
            connection = (HttpsURLConnection) optifineVersions.openConnection();
            connection.setRequestProperty("User-Agent", "Patcher OptiFine Fetcher");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String version;
                while ((version = reader.readLine()) != null) {
                    supportedOptiFineVersions.add(version);
                }
            }
        } catch (Exception e) {
            this.logger.error("Failed to read supported OptiFine versions, adding defaults.", e);
            supportedOptiFineVersions.addAll(Arrays.asList(
                "I7",
                "L5",
                "M5", "M6_pre1", "M6_pre2", "M6",
                "G5", "G6_pre1", "G6"
            ));
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void updateOptiFineGenerations() {
        HttpsURLConnection connection = null;
        try {
            URL optifineGenerations = new URL("https://static.sk1er.club/patcher/optifine_generations.json");
            connection = (HttpsURLConnection) optifineGenerations.openConnection();
            connection.setRequestProperty("User-Agent", "Patcher OptiFine Fetcher");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                generations = new Gson().fromJson(reader, OptiFineGenerations.class);
            }
        } catch (Exception e) {
            this.logger.error("Failed to read OptiFine generations list. Supplying default supported generations.", e);
            generations = new OptiFineGenerations();
            generations.getIGeneration().add("I7");

            generations.getLGeneration().add("L5");
            generations.getLGeneration().add("L6");

            generations.getMGeneration().add("M5");
            generations.getMGeneration().add("M6_pre1");
            generations.getMGeneration().add("M6_pre2");
            generations.getMGeneration().add("M6");

            generations.getGGeneration().add("G5");
            generations.getGGeneration().add("G6_pre1");
            generations.getGGeneration().add("G6");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}
