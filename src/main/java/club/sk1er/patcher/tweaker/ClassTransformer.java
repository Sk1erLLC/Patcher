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

import club.sk1er.mods.core.universal.UDesktop;
import club.sk1er.patcher.asm.*;
import club.sk1er.patcher.asm.forge.ForgeChunkManagerTransformer;
import club.sk1er.patcher.asm.forge.ModelLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.*;
import club.sk1er.patcher.tweaker.asm.forge.*;
import club.sk1er.patcher.tweaker.asm.lwjgl.KeyboardTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.WindowsDisplayTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.WindowsKeycodesTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.InventoryPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.witherfix.EntityWitherTransformer;
import club.sk1er.patcher.tweaker.asm.util.ForcePublicTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

public class ClassTransformer implements IClassTransformer {

    public static final boolean outputBytecode = "true".equals(System.getProperty("debugBytecode", "false"));
    public static String optifineVersion = "NONE";
    private final Logger logger = LogManager.getLogger("Patcher - Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader("Config");
            classReader.accept(classNode, ClassReader.SKIP_CODE);
            for (FieldNode fieldNode : classNode.fields) {
                if (fieldNode.name.equals("OF_RELEASE")) {
                    optifineVersion = (String) fieldNode.value;
                    break;
                }
            }

            final Set<String> unsupportedOptiFineVersions = Sets.newHashSet("I3", "H8", "H7", "H6", "H5");
            if (unsupportedOptiFineVersions.contains(optifineVersion)) {
                logger.info("User has outdated OptiFine. (version: OptiFine-{})", optifineVersion);
                this.haltForOptifine("OptiFine " + optifineVersion + " has been detected, which is not supported by Patcher and will crash.\n" +
                    "Please update to a newer version of OptiFine (i7 and above are supported) before trying to launch.");
                return;
            }
        } catch (IOException e) {
            logger.info("Something went wrong, or the user doesn't have optifine");
        }

        registerTransformer(new S2EPacketCloseWindowTransformer());
        registerTransformer(new EntityItemTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new GuiGameOverTransformer());
        registerTransformer(new EntityLivingBaseTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new WorldTransformer());
        registerTransformer(new ChunkTransformer());
        registerTransformer(new EntityPlayerSPTransformer());
        registerTransformer(new GuiUtilsTransformer());
        registerTransformer(new ChatStyleTransformer());
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new GuiAchievementTransformer());
        registerTransformer(new GuiScreenTransformer());
        registerTransformer(new ItemRendererTransformer());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new RenderEntityItemTransformer());
        registerTransformer(new AbstractResourcePackTransformer());
        registerTransformer(new GuiIngameTransformer());
        registerTransformer(new BlockCropsTransformer());
        registerTransformer(new BlockNetherWartTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new NBTTagStringTransformer());
        registerTransformer(new BlockRendererDispatcherTransformer());
        registerTransformer(new GuiVideoSettingsTransformer());
        registerTransformer(new WorldRendererTransformer());
        registerTransformer(new GameSettingsTransformer());
        registerTransformer(new AnvilChunkLoaderTransformer());
        registerTransformer(new FallbackResourceManagerTransformer());
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
        registerTransformer(new CommandHandlerTransformer());
        registerTransformer(new TileEntityRendererDispatcherTransformer());
        registerTransformer(new ResourcePackRepositoryTransformer());
        registerTransformer(new ServerListTransformer());
        registerTransformer(new S14PacketEntityTransformer());
        registerTransformer(new S19PacketEntityHeadLookTransformer());
        registerTransformer(new S19PacketEntityStatusTransformer());
        registerTransformer(new NodeProcessorTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new ScreenShotHelperTransformer());
        registerTransformer(new GuiScreenResourcePacksTransformer());
        registerTransformer(new TileEntityEnchantmentTableRendererTransformer());
        registerTransformer(new EntityTransformer());
        registerTransformer(new BlockRedstoneTorchTransformer());
        registerTransformer(new RenderItemTransformer());
        registerTransformer(new LayerArmorBaseTransformer());
        registerTransformer(new GameRulesValueTransformer());
        registerTransformer(new EntityOtherPlayerMPTransformer());
        registerTransformer(new S0EPacketSpawnObjectTransformer());
        registerTransformer(new RenderXPOrbTransformer());
        registerTransformer(new EntityXPOrbTransformer());
        registerTransformer(new SoundManagerTransformer());
        registerTransformer(new VisGraphTransformer());
        registerTransformer(new WorldClientTransformer());
        registerTransformer(new EntityFXTransformer());
        registerTransformer(new LongHashMapTransformer());
        registerTransformer(new ChunkCoordIntPairTransformer());
        registerTransformer(new BakedQuadTransformer());
        registerTransformer(new TexturedQuadTransformer());
        registerTransformer(new ModelRendererTransformer());
        registerTransformer(new EffectRendererTransformer());
        registerTransformer(new BlockPosTransformer());
        registerTransformer(new WorldServerTransformer());
        registerTransformer(new BlockPistonBaseTransformer());
        registerTransformer(new BlockPistonStructureHelperTransformer());
        registerTransformer(new GuiMultiplayerTransformer());
        registerTransformer(new ServerSelectionListTransformer());
        registerTransformer(new GuiOverlayDebugTransformer());
        registerTransformer(new VertexFormatTransformer());
        registerTransformer(new LayerHeldItemTransformer());
        registerTransformer(new StatBaseTransformer());
        registerTransformer(new ItemStackTransformer());
        registerTransformer(new ModelPlayerTransformer());
        registerTransformer(new ServerAddressTransformer());
        registerTransformer(new LayerSpiderEyesTransformer());
        registerTransformer(new LayerCreeperChargeTransformer());
        registerTransformer(new LayerWitherAuraTransformer());
        registerTransformer(new GuiLanguageTransformer());
        registerTransformer(new TileEntityPistonRendererTransformer());
        registerTransformer(new RenderFireballTransformer());
        registerTransformer(new RenderFishTransformer());
        registerTransformer(new RenderSnowballTransformer());
        registerTransformer(new S3FPacketCustomPayloadTransformer());
        registerTransformer(new NetHandlerPlayServerTransformer());
        registerTransformer(new C17PacketCustomPayloadTransformer());
        registerTransformer(new BlockCactusTransformer());
        registerTransformer(new BlockBrewingStandTransformer());
        registerTransformer(new LazyLoadBaseTransformer());
        registerTransformer(new ArmorStandRendererTransformer());
        registerTransformer(new BlockFluidRendererTransformer());
        registerTransformer(new GuiOptionsTransformer());
        registerTransformer(new EntityWitherTransformer());
        registerTransformer(new ThreadDownloadImageDataTransformer());
        registerTransformer(new ChunkRenderDispatcherTransformer());
//        registerTransformer(new FramebufferTransformer());
//        registerTransformer(new GlStateManagerTransformer());
        registerTransformer(new ContainerTransformer());
        registerTransformer(new ScaledResolutionTransformer());
        if (isDevelopment()) registerTransformer(new InventoryEffectRendererTransformer());

        // forge classes
        registerTransformer(new ClientCommandHandlerTransformer());
        registerTransformer(new FMLClientHandlerTransformer());
        registerTransformer(new ForgeHooksClientTransformer());
        registerTransformer(new GuiModListTransformer());
        registerTransformer(new ModClassLoaderTransformer());
        registerTransformer(new ModelLoaderTransformer());
        registerTransformer(new ForgeChunkManagerTransformer());
        registerTransformer(new FluidRegistryTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        //registerTransformer(new ASMDataTableTransformer());
        registerTransformer(new MinecraftForgeClientTransformer());
        registerTransformer(new BlockInfoTransformer());
        registerTransformer(new VertexLighterFlatTransformer());
        registerTransformer(new ForgeBlockModelRendererTransformer());
        //registerTransformer(new ModDiscovererTransformer());
        //registerTransformer(new VertexLighterSmoothAoTransformer());
        registerTransformer(new MinecraftForgeTransformer());
        //registerTransformer(new ItemLayerModelTransformer());

        // optifine
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

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            logger.error("Exception when transforming {} : {}", transformedName, e.getClass().getSimpleName(), e);
        }

        if (outputBytecode) {
            File bytecodeDirectory = new File("bytecode");
            String transformedClassName;

            // anonymous classes
            if (transformedName.contains("$")) {
                transformedClassName = transformedName.replace('$', '.') + ".class";
            } else {
                transformedClassName = transformedName + ".class";
            }

            try {
                if (!bytecodeDirectory.exists()) {
                    bytecodeDirectory.mkdirs();
                }

                File bytecodeOutput = new File(bytecodeDirectory, transformedClassName);

                if (!bytecodeOutput.exists()) {
                    bytecodeOutput.createNewFile();
                }

                try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                    os.write(classWriter.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
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
                close();
            }
        });

        Object[] options = {openOptifine, close};
        JOptionPane.showOptionDialog(null, message, "Launch Aborted", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        this.close();
    }

    private void close() {
        try {
            final Class<?> aClass = Class.forName("java.lang.Shutdown");
            final Method exit = aClass.getDeclaredMethod("exit", int.class);
            exit.setAccessible(true);
            exit.invoke(null, 0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}