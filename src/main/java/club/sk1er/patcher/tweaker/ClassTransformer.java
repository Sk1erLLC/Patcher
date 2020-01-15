package club.sk1er.patcher.tweaker;

import club.sk1er.patcher.tweaker.asm.ClientCommandHandlerTransformer;
import club.sk1er.patcher.tweaker.asm.EntityItemTransformer;
import club.sk1er.patcher.tweaker.asm.EntityLivingBaseTransformer;
import club.sk1er.patcher.tweaker.asm.GuiGameOverTransformer;
import club.sk1er.patcher.tweaker.asm.InventoryEffectRendererTransformer;
import club.sk1er.patcher.tweaker.asm.MinecraftTransformer;
import club.sk1er.patcher.tweaker.asm.RenderPlayerTransformer;
import club.sk1er.patcher.tweaker.asm.RendererLivingEntityTransformer;
import club.sk1er.patcher.tweaker.asm.S2EPacketCloseWindowTransformer;
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

    private static final Logger LOGGER = LogManager.getLogger("PatcherTransformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();
    private boolean outputBytecode = Boolean.parseBoolean(System.getProperty("debugBytecode", "false"));

    public ClassTransformer() {
        registerTransformer(new S2EPacketCloseWindowTransformer());
        registerTransformer(new EntityItemTransformer());
        registerTransformer(new MinecraftTransformer());
        registerTransformer(new GuiGameOverTransformer());
        registerTransformer(new ClientCommandHandlerTransformer());
        registerTransformer(new InventoryEffectRendererTransformer());
        registerTransformer(new EntityLivingBaseTransformer());
        registerTransformer(new RenderPlayerTransformer());
        registerTransformer(new RendererLivingEntityTransformer());
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;

        Collection<PatcherTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (PatcherTransformer transformer : transformers) {
            LOGGER.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(classNode, transformedName);
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            System.out.println("Exception when transforming " + transformedName + " : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }

        if (outputBytecode) {
            try {
                File bytecodeDirectory = new File("bytecode");
                File bytecodeOutput = new File(bytecodeDirectory, transformedName + ".class");

                if (!bytecodeDirectory.exists()) bytecodeDirectory.mkdirs();
                if (!bytecodeOutput.exists()) bytecodeOutput.createNewFile();

                FileOutputStream os = new FileOutputStream(bytecodeOutput);
                os.write(classWriter.toByteArray());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return classWriter.toByteArray();
    }
}