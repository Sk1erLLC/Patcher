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

package club.sk1er.patcher.tweaker.optifine;

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.asm.BakedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.ModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.TexturedQuadTransformer;
import club.sk1er.patcher.tweaker.asm.levelhead.LevelheadAboveHeadRenderTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineEntityRendererTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRenderItemFrameTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRenderTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRendererLivingEntityTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.I7.MapGenStructureReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.L5.ItemModelMesherReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.BakedQuadReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.ExtendedBlockStorageReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.FaceBakeryReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.ModelRotationReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.pingtag.TagRendererListenerTransformer;
import club.sk1er.patcher.tweaker.asm.pingtag.TagRendererTransformer;
import club.sk1er.patcher.tweaker.asm.rporganizer.GuiCustomResourcePacks;
import club.sk1er.patcher.tweaker.asm.tnttime.TNTTimeTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;

public class OptifineClassTransformer implements IClassTransformer {

    private final Logger LOGGER = LogManager.getLogger("Patcher - OptiFine Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();
    private final boolean outputBytecode =
        Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));

    public OptifineClassTransformer() {
        registerTransformer(new OptifineEntityRendererTransformer());
        registerTransformer(new OptifineRenderTransformer());
        registerTransformer(new OptifineRendererLivingEntityTransformer());
        registerTransformer(new OptifineRenderItemFrameTransformer());

        registerTransformer(new TagRendererTransformer());
        registerTransformer(new TagRendererListenerTransformer());
        registerTransformer(new LevelheadAboveHeadRenderTransformer());
        registerTransformer(new TNTTimeTransformer());
      
        // Reflection Optimizations
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader("Config");
            classReader.accept(classNode, ClassReader.SKIP_CODE);
            String optifineVersion = "";
            for (FieldNode fieldNode : classNode.fields) {
                if (fieldNode.name.equals("OF_RELEASE")) {
                    optifineVersion = (String) fieldNode.value;
                    break;
                }
            }
            switch (optifineVersion) {
                case "I7":
                    LOGGER.info("Found optifine I7");
                    registerCommonTransformers();
                    registerI7Transformers();
                    break;
                case "L5":
                    LOGGER.info("Found optifine L5");
                    registerCommonTransformers();
                    registerL5Transformers();
                    break;
                default:
                    LOGGER.info("User has old optifine version. Aborting reflection optimizations");
            }
        } catch (IOException e) {
            LOGGER.info("Something went wrong, or the user doesn't have optifine");
        }
    }

    private void registerCommonTransformers() {
        registerTransformer(new BakedQuadReflectionOptimizer());
        registerTransformer(new FaceBakeryReflectionOptimizer());
        registerTransformer(new ModelRotationReflectionOptimizer());
        registerTransformer(new ExtendedBlockStorageReflectionOptimizer());
    }

    private void registerI7Transformers() {
        registerTransformer(new MapGenStructureReflectionOptimizer());
    }

    private void registerL5Transformers() {
        registerTransformer(new ItemModelMesherReflectionOptimizer());

        registerTransformer(new GuiCustomResourcePacks());

        if (!(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            try {
                if (Class.forName("io.framesplus.FramesPlus") != null) {
                    LOGGER.warn("Frames+ is installed, not running BakedQuad/TexturedQuad/ModelRenderer transformation.");
                }
            } catch (Exception e) {
                LOGGER.info("Frames+ is not installed, running BakedQuad/TexturedQuad/ModelRenderer transformation.");
                registerTransformer(new BakedQuadTransformer());
                registerTransformer(new TexturedQuadTransformer());
                registerTransformer(new ModelRendererTransformer());
            }
        }
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return ClassTransformer.createTransformer(transformedName, bytes, transformerMap, LOGGER, outputBytecode);
    }
}
