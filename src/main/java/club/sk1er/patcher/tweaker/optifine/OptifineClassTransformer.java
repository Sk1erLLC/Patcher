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
import club.sk1er.patcher.tweaker.asm.levelhead.LevelheadAboveHeadRenderTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineEntityRendererTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRenderItemFrameTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRenderTransformer;
import club.sk1er.patcher.tweaker.asm.optifine.OptifineRendererLivingEntityTransformer;
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

        // mods
        registerTransformer(new TagRendererTransformer());
        registerTransformer(new TagRendererListenerTransformer());
        registerTransformer(new LevelheadAboveHeadRenderTransformer());
        registerTransformer(new TNTTimeTransformer());
        registerTransformer(new GuiCustomResourcePacks());

        try {
            if (Class.forName("io.framesplus.FramesPlus") != null) {
                LOGGER.warn("Frames+ is installed, not running BakedQuad transformation.");
            }
        } catch (Exception e) {
            LOGGER.info("Frames+ is not installed, running BakedQuad transformation.");
            registerTransformer(new BakedQuadTransformer());
        }
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return ClassTransformer.createTransformer(
            transformedName, bytes, transformerMap, LOGGER, outputBytecode);
    }
}
