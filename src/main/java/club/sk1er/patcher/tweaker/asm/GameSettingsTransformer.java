package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GameSettingsTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.settings.GameSettings"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode onGuiClosedMethod = new MethodNode(Opcodes.ACC_PUBLIC, "onGuiClosed", "()V", null, null);
        onGuiClosedMethod.instructions.add(onGuiClosed());
        classNode.methods.add(onGuiClosedMethod);

        FieldNode needsResourceRefreshField = new FieldNode(Opcodes.ACC_PRIVATE, "needsResourceRefresh", "Z", null, null);
        classNode.fields.add(needsResourceRefreshField);

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("setOptionFloatValue")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof MethodInsnNode
                            && ((MethodInsnNode) node).owner.equals("net/minecraft/client/Minecraft")
                            && ((MethodInsnNode) node).name.equals("scheduleResourcesRefresh")
                            && ((MethodInsnNode) node).desc.equals("()Lcom/google/common/util/concurrent/ListenableFuture;")) {
                        methodNode.instructions.insertBefore(node.getPrevious().getPrevious(), insertBoolean());
                    }
                }

                methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureMap",
                        "func_174937_a", // setBlurMipmapDirect
                        "(ZZ)V", false), insertBoolean());

                break;
            }
        }
    }

    private InsnList insertBoolean() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/settings/GameSettings", "needsResourceRefresh", "Z"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList onGuiClosed() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/settings/GameSettings", "needsResourceRefresh", "Z"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/settings/GameSettings", "field_74317_L", // mc
                "Lnet/minecraft/client/Minecraft;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_175603_A", // scheduleResourcesRefresh
                "()Lcom/google/common/util/concurrent/ListenableFuture;", false));
        list.add(new InsnNode(Opcodes.POP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/settings/GameSettings", "needsResourceRefresh", "Z"));
        list.add(labelNode);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
