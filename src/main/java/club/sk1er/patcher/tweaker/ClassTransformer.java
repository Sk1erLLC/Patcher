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

import club.sk1er.mods.core.util.ModCoreDesktop;
import club.sk1er.patcher.asm.BakedQuadTransformer;
import club.sk1er.patcher.asm.BlockBrewingStandTransformer;
import club.sk1er.patcher.asm.BlockPotatoTransformer;
import club.sk1er.patcher.asm.ChatStyleTransformer;
import club.sk1er.patcher.asm.ChunkCoordIntPairTransformer;
import club.sk1er.patcher.asm.EnchantmentTransformer;
import club.sk1er.patcher.asm.FallbackResourceManagerTransformer;
import club.sk1er.patcher.asm.GuiIngameTransformer;
import club.sk1er.patcher.asm.GuiPlayerTabOverlayTransformer;
import club.sk1er.patcher.asm.LongHashMapTransformer;
import club.sk1er.patcher.asm.TexturedQuadTransformer;
import club.sk1er.patcher.asm.forge.ForgeChunkManagerTransformer;
import club.sk1er.patcher.asm.forge.ModelLoaderTransformer;
import club.sk1er.patcher.asm.forge.VertexLighterSmoothAoTransformer;
import club.sk1er.patcher.tweaker.asm.AbstractResourcePackTransformer;
import club.sk1er.patcher.tweaker.asm.AnvilChunkLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.ArmorStandRendererTransformer;
import club.sk1er.patcher.tweaker.asm.BlockCactusTransformer;
import club.sk1er.patcher.tweaker.asm.BlockFluidRendererTransformer;
import club.sk1er.patcher.tweaker.asm.BlockPistonBaseTransformer;
import club.sk1er.patcher.tweaker.asm.BlockPistonStructureHelperTransformer;
import club.sk1er.patcher.tweaker.asm.BlockPosTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRedstoneTorchTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRendererDispatcherTransformer;
import club.sk1er.patcher.tweaker.asm.C01PacketChatMessageTransformer;
import club.sk1er.patcher.tweaker.asm.C17PacketCustomPayloadTransformer;
import club.sk1er.patcher.tweaker.asm.ChunkTransformer;
import club.sk1er.patcher.tweaker.asm.CommandHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.EffectRendererTransformer;
import club.sk1er.patcher.tweaker.asm.EntityFXTransformer;
import club.sk1er.patcher.tweaker.asm.EntityItemTransformer;
import club.sk1er.patcher.tweaker.asm.EntityLivingBaseTransformer;
import club.sk1er.patcher.tweaker.asm.EntityOtherPlayerMPTransformer;
import club.sk1er.patcher.tweaker.asm.EntityPlayerSPTransformer;
import club.sk1er.patcher.tweaker.asm.EntityTransformer;
import club.sk1er.patcher.tweaker.asm.EntityXPOrbTransformer;
import club.sk1er.patcher.tweaker.asm.ExtendedBlockStorageTransformer;
import club.sk1er.patcher.tweaker.asm.FontRendererTransformer;
import club.sk1er.patcher.tweaker.asm.GameRulesValueTransformer;
import club.sk1er.patcher.tweaker.asm.GameSettingsTransformer;
import club.sk1er.patcher.tweaker.asm.GuiAchievementTransformer;
import club.sk1er.patcher.tweaker.asm.GuiChatTransformer;
import club.sk1er.patcher.tweaker.asm.GuiContainerTransformer;
import club.sk1er.patcher.tweaker.asm.GuiGameOverTransformer;
import club.sk1er.patcher.tweaker.asm.GuiLanguageListTransformer;
import club.sk1er.patcher.tweaker.asm.GuiLanguageTransformer;
import club.sk1er.patcher.tweaker.asm.GuiMultiplayerTransformer;
import club.sk1er.patcher.tweaker.asm.GuiNewChatTransformer;
import club.sk1er.patcher.tweaker.asm.GuiOptionsTransformer;
import club.sk1er.patcher.tweaker.asm.GuiOverlayDebugTransformer;
import club.sk1er.patcher.tweaker.asm.GuiScreenResourcePacksTransformer;
import club.sk1er.patcher.tweaker.asm.GuiScreenTransformer;
import club.sk1er.patcher.tweaker.asm.GuiTransformer;
import club.sk1er.patcher.tweaker.asm.GuiVideoSettingsTransformer;
import club.sk1er.patcher.tweaker.asm.InventoryEffectRendererTransformer;
import club.sk1er.patcher.tweaker.asm.ItemRendererTransformer;
import club.sk1er.patcher.tweaker.asm.ItemStackTransformer;
import club.sk1er.patcher.tweaker.asm.LayerArmorBaseTransformer;
import club.sk1er.patcher.tweaker.asm.LayerArrowTransformer;
import club.sk1er.patcher.tweaker.asm.LayerCreeperChargeTransformer;
import club.sk1er.patcher.tweaker.asm.LayerCustomHeadTransformer;
import club.sk1er.patcher.tweaker.asm.LayerHeldItemTransformer;
import club.sk1er.patcher.tweaker.asm.LayerSpiderEyesTransformer;
import club.sk1er.patcher.tweaker.asm.LayerWitherAuraTransformer;
import club.sk1er.patcher.tweaker.asm.LazyLoadBaseTransformer;
import club.sk1er.patcher.tweaker.asm.MinecraftServerTransformer;
import club.sk1er.patcher.tweaker.asm.MinecraftTransformer;
import club.sk1er.patcher.tweaker.asm.ModelPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.ModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.NBTTagCompoundTransformer;
import club.sk1er.patcher.tweaker.asm.NBTTagStringTransformer;
import club.sk1er.patcher.tweaker.asm.NetHandlerPlayClientTransformer;
import club.sk1er.patcher.tweaker.asm.NetHandlerPlayServerTransformer;
import club.sk1er.patcher.tweaker.asm.NodeProcessorTransformer;
import club.sk1er.patcher.tweaker.asm.RenderArrowTransformer;
import club.sk1er.patcher.tweaker.asm.RenderEntityItemTransformer;
import club.sk1er.patcher.tweaker.asm.RenderFireballTransformer;
import club.sk1er.patcher.tweaker.asm.RenderFishTransformer;
import club.sk1er.patcher.tweaker.asm.RenderGlobalTransformer;
import club.sk1er.patcher.tweaker.asm.RenderItemFrameTransformer;
import club.sk1er.patcher.tweaker.asm.RenderItemTransformer;
import club.sk1er.patcher.tweaker.asm.RenderPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.RenderSnowballTransformer;
import club.sk1er.patcher.tweaker.asm.RenderXPOrbTransformer;
import club.sk1er.patcher.tweaker.asm.ResourcePackRepositoryTransformer;
import club.sk1er.patcher.tweaker.asm.S0EPacketSpawnObjectTransformer;
import club.sk1er.patcher.tweaker.asm.S14PacketEntityTransformer;
import club.sk1er.patcher.tweaker.asm.S19PacketEntityHeadLookTransformer;
import club.sk1er.patcher.tweaker.asm.S19PacketEntityStatusTransformer;
import club.sk1er.patcher.tweaker.asm.S2EPacketCloseWindowTransformer;
import club.sk1er.patcher.tweaker.asm.S3FPacketCustomPayloadTransformer;
import club.sk1er.patcher.tweaker.asm.ScoreboardTransformer;
import club.sk1er.patcher.tweaker.asm.ScreenShotHelperTransformer;
import club.sk1er.patcher.tweaker.asm.ServerAddressTransformer;
import club.sk1er.patcher.tweaker.asm.ServerListTransformer;
import club.sk1er.patcher.tweaker.asm.ServerSelectionListTransformer;
import club.sk1er.patcher.tweaker.asm.SoundManagerTransformer;
import club.sk1er.patcher.tweaker.asm.StatBaseTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityEnchantmentTableRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityEndPortalRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityPistonRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityRendererDispatcherTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntitySkullRendererTransformer;
import club.sk1er.patcher.tweaker.asm.VertexFormatTransformer;
import club.sk1er.patcher.tweaker.asm.VisGraphTransformer;
import club.sk1er.patcher.tweaker.asm.WorldClientTransformer;
import club.sk1er.patcher.tweaker.asm.WorldRendererTransformer;
import club.sk1er.patcher.tweaker.asm.WorldServerTransformer;
import club.sk1er.patcher.tweaker.asm.WorldTransformer;
import club.sk1er.patcher.tweaker.asm.forge.BlockInfoTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ClientCommandHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.forge.FMLClientHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.forge.FluidRegistryTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ForgeBlockModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ForgeHooksClientTransformer;
import club.sk1er.patcher.tweaker.asm.forge.GuiIngameForgeTransformer;
import club.sk1er.patcher.tweaker.asm.forge.GuiModListTransformer;
import club.sk1er.patcher.tweaker.asm.forge.MinecraftForgeClientTransformer;
import club.sk1er.patcher.tweaker.asm.forge.MinecraftForgeTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ModClassLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.forge.VertexLighterFlatTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.KeyboardTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.WindowsDisplayTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.WindowsKeycodesTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.InventoryPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.util.ForcePublicTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClassTransformer implements IClassTransformer {

    public static final boolean outputBytecode = Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));
    public static String optifineVersion = "NONE";
    private final Logger LOGGER = LogManager.getLogger("Patcher - Class Transformer");
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

            final List<String> unsupportedOptiFineVersions = Arrays.asList("I3", "H8", "H7", "H6", "H5");
            if (unsupportedOptiFineVersions.contains(optifineVersion)) {
                LOGGER.info("User has outdated OptiFine. (version: OptiFine-{})", optifineVersion);
                this.halt("OptiFine " + optifineVersion + " has been detected, which is not supported by Patcher and will crash.\n" +
                    "Please update to a newer version of OptiFine (i7 and above are supported) before trying to launch.");
                return;
            }
        } catch (IOException e) {
            LOGGER.info("Something went wrong, or the user doesn't have optifine");
        }

        registerTransformer(new S2EPacketCloseWindowTransformer());
        registerTransformer(new EntityItemTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new GuiGameOverTransformer());
        registerTransformer(new InventoryEffectRendererTransformer());
        registerTransformer(new EntityLivingBaseTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new WorldTransformer());
        registerTransformer(new ChunkTransformer());
        registerTransformer(new EntityPlayerSPTransformer());
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
        registerTransformer(new BlockPotatoTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new NBTTagStringTransformer());
        registerTransformer(new BlockRendererDispatcherTransformer());
        registerTransformer(new GuiVideoSettingsTransformer());
        registerTransformer(new WorldRendererTransformer());
        registerTransformer(new GameSettingsTransformer());
        registerTransformer(new AnvilChunkLoaderTransformer());
        registerTransformer(new FallbackResourceManagerTransformer());
        registerTransformer(new RenderArrowTransformer());
        registerTransformer(new RenderItemFrameTransformer());
        registerTransformer(new TileEntitySkullRendererTransformer());
        registerTransformer(new TileEntityEndPortalRendererTransformer());
        registerTransformer(new GuiContainerTransformer());
        registerTransformer(new EnchantmentTransformer());
        registerTransformer(new MinecraftServerTransformer());
        registerTransformer(new FontRendererTransformer());
        registerTransformer(new GuiLanguageListTransformer());
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
        registerTransformer(new GuiTransformer());
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
        registerTransformer(new ExtendedBlockStorageTransformer());
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
        registerTransformer(new VertexLighterSmoothAoTransformer());
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

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

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

            File bytecodeOutput = new File(bytecodeDirectory, transformedClassName);

            try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                os.write(classWriter.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
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
        return createTransformer(transformedName, bytes, transformerMap, LOGGER);
    }

    private void halt(final String message) {
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
                    ModCoreDesktop.INSTANCE.browse(new URI("https://optifine.net/downloads/"));
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
}