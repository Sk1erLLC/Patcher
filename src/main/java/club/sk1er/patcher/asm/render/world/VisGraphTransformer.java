package club.sk1er.patcher.asm.render.world;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class VisGraphTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.chunk.VisGraph"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "patcherLimitScan", "Z", null, null));

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("computeVisibility") || methodName.equals("func_178607_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 256) {
                        LabelNode gotoInsn = new LabelNode();
                        methodNode.instructions.insertBefore(next, changeOperand(gotoInsn));
                        methodNode.instructions.insertBefore(next.getNext(), gotoInsn);
                        break;
                    }
                }
            }
        }
    }

    private InsnList changeOperand(LabelNode gotoInsn) {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("cullingFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 4097));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        return list;
    }
}
