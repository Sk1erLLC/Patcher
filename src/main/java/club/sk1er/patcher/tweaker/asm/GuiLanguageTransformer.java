package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiLanguageTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiLanguage$List"};
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

            if (methodName.equals("elementClicked")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("refreshResources")) {
                        methodNode.instructions.remove(next.getPrevious());
                        methodNode.instructions.remove(next.getPrevious());

                        methodNode.instructions.insertBefore(next, reloadLanguagesOnly());
                        methodNode.instructions.remove(next);
                    }
                }
            }
        }
    }

    private InsnList reloadLanguagesOnly() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "getLanguageManager",
                "()Lnet/minecraft/client/resources/LanguageManager;", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiLanguage$List", "mc", "Lnet/minecraft/client/Minecraft;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "getResourceManager",
                "()Lnet/minecraft/client/resources/IResourceManager;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, " net/minecraft/client/resources/LanguageManager", "onResourceManagerReload",
                "(Lnet/minecraft/client/resources/IResourceManager;)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
