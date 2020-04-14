package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class RenderItemFrameTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.tileentity.RenderItemFrame"};
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

            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), cancelRendering());
            } else if (methodName.equals("renderName") || methodName.equals("func_177067_a")) {
                makeNametagTransparent(methodNode);
            }
        }
    }

    private InsnList cancelRendering() {
        InsnList list = new InsnList();
        list.add(
            new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "disableItemFrames", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private void makeNametagTransparent(MethodNode methodNode) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        LabelNode afterDraw = new LabelNode();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                String nodeName = mapMethodNameFromNode((MethodInsnNode) node);
                if (nodeName.equals("begin") || nodeName.equals("func_181668_a")) {
                    AbstractInsnNode prevNode = node.getPrevious().getPrevious().getPrevious();
                    methodNode.instructions.insertBefore(prevNode,
                        new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentNameTags",
                            "Z"));
                    methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, afterDraw));
                } else if (nodeName.equals("draw") || nodeName.equals("func_78381_a")) {
                    methodNode.instructions.insert(node, afterDraw);
                    break;
                }
            }
        }
    }
}
