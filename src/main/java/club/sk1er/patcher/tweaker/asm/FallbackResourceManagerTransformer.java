package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FallbackResourceManagerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.resources.FallbackResourceManager"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (methodName.equals("getResource") || methodName.equals("func_110536_a")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getFastCache());
                break;
            }
        }
    }

    private InsnList getFastCache() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/hooks/FallbackResourceManagerHook",
                "getCachedResource",
                "(Lnet/minecraft/client/resources/FallbackResourceManager;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/resources/IResource;",
                false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
