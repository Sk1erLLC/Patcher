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

package club.sk1er.patcher.tweaker.other;

import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.asm.InventoryEffectRendererTransformer;
import club.sk1er.patcher.tweaker.asm.levelhead.LevelheadAboveHeadRenderTransformer;
import club.sk1er.patcher.tweaker.asm.modcore.ModCoreModelRendererTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.*;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.BakedQuadReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.EntityRendererReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.ExtendedBlockStorageReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.FaceBakeryReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common.ModelRotationReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.modern.ItemModelMesherReflectionOptimizer;
import club.sk1er.patcher.tweaker.asm.optifine.signfix.GuiEditSignTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.signfix.TileEntitySignRendererTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.witherfix.EntityWitherTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.xpfix.GuiIngameForgeTransformer;
import club.sk1er.patcher.tweaker.asm.pingtag.TagRendererListenerTransformer;
import club.sk1er.patcher.tweaker.asm.pingtag.TagRendererTransformer;
import club.sk1er.patcher.tweaker.asm.rporganizer.GuiCustomResourcePacks;
import club.sk1er.patcher.tweaker.asm.tnttime.TNTTimeTransformer;
import club.sk1er.patcher.tweaker.asm.ve.BetterChatTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used for editing other mods (OptiFine, LevelHead, TNT Timer, etc) after they've loaded.
 */
public class ModClassTransformer implements IClassTransformer {

    private final Logger logger = LogManager.getLogger("Patcher - Mod Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();

    public ModClassTransformer() {
        // OptiFine loads these classes after we do, overwriting our changes,
        // so transform it AFTER OptiFine loads.
        registerTransformer(new EntityRendererTransformer());
        registerTransformer(new RenderTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
        registerTransformer(new RenderItemFrameTransformer());

        // PingTag by Powns
        registerTransformer(new TagRendererTransformer());
        registerTransformer(new TagRendererListenerTransformer());

        // LevelHead by Sk1er LLC (i know that guy!)
        registerTransformer(new LevelheadAboveHeadRenderTransformer());

        // TNT Timer by Sk1er LLC
        registerTransformer(new TNTTimeTransformer());

        // ResourcePackOrganizer by chylex & ResourcePackManager by aycy (shares similar method)
        registerTransformer(new GuiCustomResourcePacks());

        // Vanilla Enhancements by OrangeMarshall
        registerTransformer(new BetterChatTransformer());

        // ModCore
        registerTransformer(new ModCoreModelRendererTransformer());

        // SpiderFrog's oam overwrites our injection (because surely a 1.7 animations mod needs this feature)
        // only run in this transformer if it's loaded in prod
        if (!isDevelopment()) registerTransformer(new InventoryEffectRendererTransformer());

        // OptiFine uses Reflection for compatibility between Forge & itself,
        // and since we know they're using Forge, we're able to change methods back
        // to how they normally were (using Forge's changes).
        //
        // Only I7, L5, and L6 are supported due to them being the biggest 1.8.9 versions of OptiFine.
        final String optifineVersion = ClassTransformer.optifineVersion;
        if (optifineVersion.equals("I7")) {
            logger.info("Found OptiFine I7");
            registerCommonTransformers();
            registerI7Transformers();
        } else if (optifineVersion.startsWith("L6") || optifineVersion.equals("L5")) {
            logger.info("Found OptiFine " + optifineVersion);
            registerCommonTransformers();
            registerLSeriesTransformers();
            registerLSeriesFixesTransformers();
        } else if (optifineVersion.startsWith("M5")) {
            logger.info("Found OptiFine M5");
            registerCommonTransformers();
            registerLSeriesTransformers();
        } else {
            logger.info("User has either an old OptiFine version, or no OptiFine present. Aborting reflection optimizations.");
        }
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return ClassTransformer.createTransformer(transformedName, bytes, transformerMap, logger);
    }

    private void registerCommonTransformers() {
        registerTransformer(new BakedQuadReflectionOptimizer());
        registerTransformer(new FaceBakeryReflectionOptimizer());
        registerTransformer(new ModelRotationReflectionOptimizer());
        registerTransformer(new ExtendedBlockStorageReflectionOptimizer());
        registerTransformer(new EntityRendererReflectionOptimizer());

        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new OptifineFontRendererTransformer());
        registerTransformer(new FontRendererHookTransformer());
        registerTransformer(new FullbrightTickerTransformer());
        registerTransformer(new EntityCullingTransformer());
    }

    private void registerI7Transformers() {
        //registerTransformer(new MapGenStructureReflectionOptimizer());
    }

    private void registerLSeriesTransformers() {
        registerTransformer(new ItemModelMesherReflectionOptimizer());

        registerTransformer(new GuiDetailSettingsOFTransformer());
    }

    private void registerLSeriesFixesTransformers() {
        registerTransformer(new GuiEditSignTransformer());
        registerTransformer(new TileEntitySignRendererTransformer());
        registerTransformer(new RandomEntitiesTransformer());
        registerTransformer(new EntityWitherTransformer());
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}
