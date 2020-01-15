package club.sk1er.patcher.tweaker.transform;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    String[] getClassName();

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    void transform(ClassNode classNode, String name);

    /**
     * Map the method name from notch names
     *
     * @param classNode  the transformed class node
     * @param methodNode the transformed classes method node
     * @return a mapped method name
     */
    default String mapMethodName(ClassNode classNode, MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
    }

    /**
     * Map the method desc from notch names
     *
     * @param methodNode the transformed method node
     * @return a mapped method desc
     */
    default String mapMethodDesc(MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(methodNode.desc);
    }

    default String getPatcherConfigClass() {
        return "club/sk1er/patcher/config/PatcherConfig";
    }
}
