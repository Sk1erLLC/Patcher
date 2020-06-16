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
import club.sk1er.patcher.tweaker.asm.levelhead.LevelheadAboveHeadRenderTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.*;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        registerTransformer(new OptifineFontRendererTransformer());
        registerTransformer(new FontRendererHookTransformer());
        registerTransformer(new TagRendererTransformer());
        registerTransformer(new TagRendererListenerTransformer());
        registerTransformer(new LevelheadAboveHeadRenderTransformer());
        registerTransformer(new TNTTimeTransformer());
        registerTransformer(new GuiCustomResourcePacks());

        // Reflection Optimizations
        switch (ClassTransformer.optifineVersion) {
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
    }
}
