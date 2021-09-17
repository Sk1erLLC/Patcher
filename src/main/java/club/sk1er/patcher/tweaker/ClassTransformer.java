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

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.asm.client.EnchantmentTransformer;
import club.sk1er.patcher.asm.client.LongHashMapTransformer;
import club.sk1er.patcher.asm.client.MinecraftTransformer;
import club.sk1er.patcher.asm.client.block.BlockBrewingStandTransformer;
import club.sk1er.patcher.asm.client.block.BlockCropsTransformer;
import club.sk1er.patcher.asm.client.block.BlockNetherWartTransformer;
import club.sk1er.patcher.asm.client.chat.ChatStyleTransformer;
import club.sk1er.patcher.asm.external.forge.ForgeChunkManagerTransformer;
import club.sk1er.patcher.asm.external.forge.ModelLoaderTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ASMModParserTransformer;
import club.sk1er.patcher.asm.external.forge.loader.MinecraftForgeTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ModClassLoaderTransformer;
import club.sk1er.patcher.asm.external.forge.loader.ModContainerFactoryTransformer;
import club.sk1er.patcher.asm.external.forge.render.ForgeHooksClientTransformer;
import club.sk1er.patcher.asm.external.forge.render.MinecraftForgeClientTransformer;
import club.sk1er.patcher.asm.external.forge.render.block.BlockInfoTransformer;
import club.sk1er.patcher.asm.external.forge.render.block.ForgeBlockModelRendererTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.LightUtilTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.VertexLighterFlatTransformer;
import club.sk1er.patcher.asm.external.forge.render.lighting.VertexLighterSmoothAoTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiIngameForgeTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiModListTransformer;
import club.sk1er.patcher.asm.external.forge.render.screen.GuiUtilsTransformer;
import club.sk1er.patcher.asm.external.lwjgl.KeyboardTransformer;
import club.sk1er.patcher.asm.external.lwjgl.WindowsDisplayTransformer;
import club.sk1er.patcher.asm.external.lwjgl.WindowsKeycodesTransformer;
import club.sk1er.patcher.asm.external.mods.optifine.InventoryPlayerTransformer;
import club.sk1er.patcher.asm.external.mods.optifine.witherfix.EntityWitherTransformer;
import club.sk1er.patcher.asm.external.util.ForcePublicTransformer;
import club.sk1er.patcher.asm.network.MinecraftServerTransformer;
import club.sk1er.patcher.asm.network.NetHandlerPlayClientTransformer;
import club.sk1er.patcher.asm.network.NetHandlerPlayServerTransformer;
import club.sk1er.patcher.asm.network.packet.C01PacketChatMessageTransformer;
import club.sk1er.patcher.asm.network.packet.S0EPacketSpawnObjectTransformer;
import club.sk1er.patcher.asm.network.packet.S14PacketEntityTransformer;
import club.sk1er.patcher.asm.network.packet.S19PacketEntityHeadLookTransformer;
import club.sk1er.patcher.asm.network.packet.S19PacketEntityStatusTransformer;
import club.sk1er.patcher.asm.network.packet.S34PacketMapsTransformer;
import club.sk1er.patcher.asm.network.packet.S3FPacketCustomPayloadTransformer;
import club.sk1er.patcher.asm.render.block.BakedQuadTransformer;
import club.sk1er.patcher.asm.render.block.TexturedQuadTransformer;
import club.sk1er.patcher.asm.render.item.ItemStackTransformer;
import club.sk1er.patcher.asm.render.particle.EffectRendererTransformer;
import club.sk1er.patcher.asm.render.screen.*;
import club.sk1er.patcher.asm.render.world.RenderGlobalTransformer;
import club.sk1er.patcher.asm.render.world.VertexFormatTransformer;
import club.sk1er.patcher.asm.render.world.VisGraphTransformer;
import club.sk1er.patcher.asm.render.world.WorldRendererTransformer;
import club.sk1er.patcher.asm.render.world.entity.*;
import club.sk1er.patcher.asm.render.world.entity.model.ModelRendererTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntityBannerRendererTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntityEnchantmentTableRendererTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntityEndPortalRendererTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntityPistonRendererTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntityRendererDispatcherTransformer;
import club.sk1er.patcher.asm.render.world.tileentity.TileEntitySkullRendererTransformer;
import club.sk1er.patcher.asm.resources.GameSettingsTransformer;
import club.sk1er.patcher.asm.resources.ScreenShotHelperTransformer;
import club.sk1er.patcher.asm.resources.SoundManagerTransformer;
import club.sk1er.patcher.asm.world.AnvilChunkLoaderTransformer;
import club.sk1er.patcher.asm.world.BlockPosTransformer;
import club.sk1er.patcher.asm.world.ChunkCoordIntPairTransformer;
import club.sk1er.patcher.asm.world.ChunkTransformer;
import club.sk1er.patcher.asm.world.GameRulesValueTransformer;
import club.sk1er.patcher.asm.world.ScoreboardTransformer;
import club.sk1er.patcher.asm.world.StatBaseTransformer;
import club.sk1er.patcher.asm.world.entity.EntityItemTransformer;
import club.sk1er.patcher.asm.world.entity.EntityLivingBaseTransformer;
import club.sk1er.patcher.asm.world.entity.EntityOtherPlayerMPTransformer;
import club.sk1er.patcher.asm.world.entity.EntityPlayerTransformer;
import club.sk1er.patcher.asm.world.entity.EntityTransformer;
import club.sk1er.patcher.asm.world.entity.data.NodeProcessorTransformer;
import club.sk1er.patcher.asm.world.entity.data.nbt.NBTTagCompoundTransformer;
import club.sk1er.patcher.asm.world.entity.data.nbt.NBTTagStringTransformer;
import club.sk1er.patcher.optifine.OptiFineGenerations;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import gg.essential.universal.UDesktop;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
                this.haltForOptifine("OptiFine " + optifineVersion + " has been detected, which is not supported by Patcher and will crash.\n" +
                    "Please update to a newer version of OptiFine (i7 and above are supported) before trying to launch.");
                return;
            }
        } catch (IOException ignored) {
        }

        registerTransformer(new EntityItemTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new EntityLivingBaseTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new ChunkTransformer());
        registerTransformer(new GuiUtilsTransformer());
        registerTransformer(new ChatStyleTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new RenderEntityItemTransformer());
        registerTransformer(new GuiIngameTransformer());
        registerTransformer(new BlockCropsTransformer());
        registerTransformer(new BlockNetherWartTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new NBTTagStringTransformer());
        registerTransformer(new GuiVideoSettingsTransformer());
        registerTransformer(new WorldRendererTransformer());
        registerTransformer(new GameSettingsTransformer());
        registerTransformer(new AnvilChunkLoaderTransformer());
        registerTransformer(new RenderArrowTransformer());
        registerTransformer(new TileEntitySkullRendererTransformer());
        registerTransformer(new TileEntityEndPortalRendererTransformer());
        registerTransformer(new GuiContainerTransformer());
        registerTransformer(new EnchantmentTransformer());
        registerTransformer(new MinecraftServerTransformer());
        registerTransformer(new FontRendererTransformer());
        registerTransformer(new LayerCustomHeadTransformer());
        registerTransformer(new NBTTagCompoundTransformer());
        registerTransformer(new GuiChatTransformer());
        registerTransformer(new C01PacketChatMessageTransformer());
        registerTransformer(new LayerArrowTransformer());
        registerTransformer(new TileEntityRendererDispatcherTransformer());
        registerTransformer(new S14PacketEntityTransformer());
        registerTransformer(new S19PacketEntityHeadLookTransformer());
        registerTransformer(new S19PacketEntityStatusTransformer());
        registerTransformer(new NodeProcessorTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new ScreenShotHelperTransformer());
        registerTransformer(new TileEntityEnchantmentTableRendererTransformer());
        registerTransformer(new EntityTransformer());
        registerTransformer(new GameRulesValueTransformer());
        registerTransformer(new EntityOtherPlayerMPTransformer());
        registerTransformer(new S0EPacketSpawnObjectTransformer());
        registerTransformer(new RenderXPOrbTransformer());
        registerTransformer(new EntityXPOrbTransformer());
        registerTransformer(new SoundManagerTransformer());
        registerTransformer(new VisGraphTransformer());
        registerTransformer(new LongHashMapTransformer());
        registerTransformer(new ChunkCoordIntPairTransformer());
        registerTransformer(new BakedQuadTransformer());
        registerTransformer(new TexturedQuadTransformer());
        registerTransformer(new ModelRendererTransformer());
        registerTransformer(new EffectRendererTransformer());
        registerTransformer(new BlockPosTransformer());
        registerTransformer(new GuiOverlayDebugTransformer());
        registerTransformer(new VertexFormatTransformer());
        registerTransformer(new LayerHeldItemTransformer());
        registerTransformer(new StatBaseTransformer());
        registerTransformer(new ItemStackTransformer());
        registerTransformer(new LayerSpiderEyesTransformer());
        registerTransformer(new LayerCreeperChargeTransformer());
        registerTransformer(new LayerWitherAuraTransformer());
        registerTransformer(new TileEntityPistonRendererTransformer());
        registerTransformer(new RenderFireballTransformer());
        registerTransformer(new RenderFishTransformer());
        registerTransformer(new RenderSnowballTransformer());
        registerTransformer(new S3FPacketCustomPayloadTransformer());
        registerTransformer(new NetHandlerPlayServerTransformer());
        registerTransformer(new BlockBrewingStandTransformer());
        registerTransformer(new ArmorStandRendererTransformer());
        registerTransformer(new EntityWitherTransformer());
        registerTransformer(new TileEntityBannerRendererTransformer());
        //registerTransformer(new RecipeBookCloningTransformer());
        registerTransformer(new RenderTNTPrimedTransformer());
        registerTransformer(new S34PacketMapsTransformer());
        registerTransformer(new EntityPlayerTransformer());
        registerTransformer(new RenderWitherTransformer());
        registerTransformer(new GuiScreenOptionsSoundsTransformer());
        if (isDevelopment()) registerTransformer(new InventoryEffectRendererTransformer());

        // forge classes
        registerTransformer(new ForgeHooksClientTransformer());
        registerTransformer(new GuiModListTransformer());
        registerTransformer(new ModClassLoaderTransformer());
        registerTransformer(new ModelLoaderTransformer());
        registerTransformer(new ForgeChunkManagerTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new MinecraftForgeClientTransformer());
        registerTransformer(new BlockInfoTransformer());
        registerTransformer(new VertexLighterFlatTransformer());
        registerTransformer(new VertexLighterSmoothAoTransformer());
        registerTransformer(new ForgeBlockModelRendererTransformer());
        registerTransformer(new MinecraftForgeTransformer());
        registerTransformer(new ASMModParserTransformer());
        registerTransformer(new LightUtilTransformer());
        registerTransformer(new ModContainerFactoryTransformer());
        //registerTransformer(new JarDiscovererTransformer());
        registerTransformer(new InventoryPlayerTransformer());

        // lwjgl
        registerTransformer(new WindowsDisplayTransformer());
        registerTransformer(new WindowsKeycodesTransformer());
        registerTransformer(new KeyboardTransformer());

        registerTransformer(new ForcePublicTransformer());
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
                    Patcher.instance.getLogger().error("Failed to create bytecode output for {}.", transformedName, e);
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

    private void haltForOptifine(String message) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        JOptionPane.showOptionDialog(null, message, "Launch Aborted", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        PatcherTweaker.invokeExit();
    }

    private void fetchSupportedOptiFineVersions() {
        HttpsURLConnection connection = null;
        try {
            final URL optifineVersions = new URL("https://static.sk1er.club/patcher/optifine.txt");
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
            supportedOptiFineVersions.addAll(Arrays.asList("I7", "L5", "M5", "M6_pre1", "M6"));
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void updateOptiFineGenerations() {
        HttpsURLConnection connection = null;
        try {
            final URL optifineGenerations = new URL("https://static.sk1er.club/patcher/optifine_generations.json");
            connection = (HttpsURLConnection) optifineGenerations.openConnection();
            connection.setRequestProperty("User-Agent", "Patcher OptiFine Fetcher");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (final Reader reader = new InputStreamReader(connection.getInputStream())) {
                generations = new Gson().fromJson(reader, OptiFineGenerations.class);
            }
        } catch (Exception e) {
            this.logger.error("Failed to read OptiFine generations list. Supplying default supported generations.", e);
            generations = new OptiFineGenerations();
            generations.getIGeneration().add("I7");

            generations.getLGeneration().add("L5");
            generations.getLGeneration().add("L6");

            generations.getMGeneration().add("M5");
            generations.getMGeneration().add("M6-pre1");
            generations.getMGeneration().add("M6");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}