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

import club.sk1er.patcher.asm.forge.ContainerTypeTransformer;
import club.sk1er.patcher.tweaker.asm.AbstractResourcePackTransformer;
import club.sk1er.patcher.tweaker.asm.AnvilChunkLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.BakedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRedstoneTorchTransformer;
import club.sk1er.patcher.tweaker.asm.BlockRendererDispatcherTransformer;
import club.sk1er.patcher.tweaker.asm.C01PacketChatMessageTransformer;
import club.sk1er.patcher.tweaker.asm.ChunkCoordIntPairTransformer;
import club.sk1er.patcher.tweaker.asm.ChunkTransformer;
import club.sk1er.patcher.tweaker.asm.CommandHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.EnchantmentTransformer;
import club.sk1er.patcher.tweaker.asm.EntityDiggingFXTransformer;
import club.sk1er.patcher.tweaker.asm.EntityFXTransformer;
import club.sk1er.patcher.tweaker.asm.EntityItemTransformer;
import club.sk1er.patcher.tweaker.asm.EntityLivingBaseTransformer;
import club.sk1er.patcher.tweaker.asm.EntityOtherPlayerMPTransformer;
import club.sk1er.patcher.tweaker.asm.EntityPlayerSPTransformer;
import club.sk1er.patcher.tweaker.asm.EntityTransformer;
import club.sk1er.patcher.tweaker.asm.EntityXPOrbTransformer;
import club.sk1er.patcher.tweaker.asm.FallbackResourceManagerTransformer;
import club.sk1er.patcher.tweaker.asm.FontRendererTransformer;
import club.sk1er.patcher.tweaker.asm.GameRulesValueTransformer;
import club.sk1er.patcher.tweaker.asm.GameSettingsTransformer;
import club.sk1er.patcher.tweaker.asm.GuiAchievementTransformer;
import club.sk1er.patcher.tweaker.asm.GuiChatTransformer;
import club.sk1er.patcher.tweaker.asm.GuiContainerTransformer;
import club.sk1er.patcher.tweaker.asm.GuiGameOverTransformer;
import club.sk1er.patcher.tweaker.asm.GuiIngameTransformer;
import club.sk1er.patcher.tweaker.asm.GuiLanguageListTransformer;
import club.sk1er.patcher.tweaker.asm.GuiNewChatTransformer;
import club.sk1er.patcher.tweaker.asm.GuiPlayerTabOverlayTransformer;
import club.sk1er.patcher.tweaker.asm.GuiScreenResourcePacksTransformer;
import club.sk1er.patcher.tweaker.asm.GuiScreenTransformer;
import club.sk1er.patcher.tweaker.asm.GuiVideoSettingsTransformer;
import club.sk1er.patcher.tweaker.asm.InventoryEffectRendererTransformer;
import club.sk1er.patcher.tweaker.asm.ItemRendererTransformer;
import club.sk1er.patcher.tweaker.asm.LayerArmorBaseTransformer;
import club.sk1er.patcher.tweaker.asm.LayerArrowTransformer;
import club.sk1er.patcher.tweaker.asm.LayerCustomHeadTransformer;
import club.sk1er.patcher.tweaker.asm.LongHashMapTransformer;
import club.sk1er.patcher.tweaker.asm.MinecraftServerTransformer;
import club.sk1er.patcher.tweaker.asm.MinecraftTransformer;
import club.sk1er.patcher.tweaker.asm.ModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.NBTTagCompoundTransformer;
import club.sk1er.patcher.tweaker.asm.NetHandlerPlayClientTransformer;
import club.sk1er.patcher.tweaker.asm.NodeProcessorTransformer;
import club.sk1er.patcher.tweaker.asm.RenderArrowTransformer;
import club.sk1er.patcher.tweaker.asm.RenderGlobalTransformer;
import club.sk1er.patcher.tweaker.asm.RenderItemFrameTransformer;
import club.sk1er.patcher.tweaker.asm.RenderItemTransformer;
import club.sk1er.patcher.tweaker.asm.RenderPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.RenderXPOrbTransformer;
import club.sk1er.patcher.tweaker.asm.RendererLivingEntityTransformer;
import club.sk1er.patcher.tweaker.asm.ResourcePackRepositoryTransformer;
import club.sk1er.patcher.tweaker.asm.S0EPacketSpawnObjectTransformer;
import club.sk1er.patcher.tweaker.asm.S14PacketEntityTransformer;
import club.sk1er.patcher.tweaker.asm.S19PacketEntityHeadLookTransformer;
import club.sk1er.patcher.tweaker.asm.S19PacketEntityStatusTransformer;
import club.sk1er.patcher.tweaker.asm.S2EPacketCloseWindowTransformer;
import club.sk1er.patcher.tweaker.asm.ScoreboardTransformer;
import club.sk1er.patcher.tweaker.asm.ScreenShotHelperTransformer;
import club.sk1er.patcher.tweaker.asm.ServerListTransformer;
import club.sk1er.patcher.tweaker.asm.SoundManagerTransformer;
import club.sk1er.patcher.tweaker.asm.TexturedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityEnchantmentTableRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityEndPortalRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntityRendererDispatcherTransformer;
import club.sk1er.patcher.tweaker.asm.TileEntitySkullRendererTransformer;
import club.sk1er.patcher.tweaker.asm.VisGraphTransformer;
import club.sk1er.patcher.tweaker.asm.WorldClientTransformer;
import club.sk1er.patcher.tweaker.asm.WorldTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ASMDataTableTransformer;
import club.sk1er.patcher.tweaker.asm.forge.BlockInfoTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ClientCommandHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.forge.FMLClientHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.forge.FluidRegistryTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ForgeBlockModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ForgeChunkManagerTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ForgeHooksClientTransformer;
import club.sk1er.patcher.tweaker.asm.forge.GuiIngameForgeTransformer;
import club.sk1er.patcher.tweaker.asm.forge.GuiModListTransformer;
import club.sk1er.patcher.tweaker.asm.forge.MinecraftForgeClientTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ModClassLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.forge.ModelLoaderTransformer;
import club.sk1er.patcher.tweaker.asm.forge.VertexLighterFlatTransformer;
import club.sk1er.patcher.tweaker.asm.lwjgl.WindowsDisplayTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.InventoryPlayerTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ClassTransformer implements IClassTransformer {

    private final Logger LOGGER = LogManager.getLogger("Patcher - Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();
    private final boolean outputBytecode = Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));

    public ClassTransformer() {
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
        registerTransformer(new ScoreboardTransformer());
        registerTransformer(new GuiAchievementTransformer());
        registerTransformer(new GuiScreenTransformer());
        registerTransformer(new ItemRendererTransformer());
        registerTransformer(new GuiNewChatTransformer());
        registerTransformer(new GuiPlayerTabOverlayTransformer());
        registerTransformer(new AbstractResourcePackTransformer());
        registerTransformer(new GuiIngameTransformer());
        registerTransformer(new NetHandlerPlayClientTransformer());
        registerTransformer(new BlockRendererDispatcherTransformer());
        registerTransformer(new GuiVideoSettingsTransformer());
        registerTransformer(new GameSettingsTransformer());
        registerTransformer(new AnvilChunkLoaderTransformer());
        registerTransformer(new FallbackResourceManagerTransformer());
        registerTransformer(new RenderArrowTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
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
        registerTransformer(new WindowsDisplayTransformer());
        registerTransformer(new S19PacketEntityHeadLookTransformer());
        registerTransformer(new S19PacketEntityStatusTransformer());
        registerTransformer(new NodeProcessorTransformer());
        registerTransformer(new RenderGlobalTransformer());
        registerTransformer(new EntityDiggingFXTransformer());
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
        registerTransformer(new ASMDataTableTransformer());
        registerTransformer(new MinecraftForgeClientTransformer());
        registerTransformer(new BlockInfoTransformer());
        registerTransformer(new VertexLighterFlatTransformer());
        registerTransformer(new ForgeBlockModelRendererTransformer());
        registerTransformer(new ContainerTypeTransformer());

        // optifine
        registerTransformer(new InventoryPlayerTransformer());

        // disabled
        //registerTransformer(new BlockDoublePlantTransformer());
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return createTransformer(transformedName, bytes, transformerMap, LOGGER, outputBytecode);
    }

    public static byte[] createTransformer(String transformedName, byte[] bytes, Multimap<String, PatcherTransformer> transformerMap, Logger logger, boolean outputBytecode) {
        if (bytes == null) return null;

        Collection<PatcherTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        logger.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (PatcherTransformer transformer : transformers) {
            logger.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
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
}