package club.sk1er.patcher.tweaker.transform;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Used to string across multiple transformers that all do the same thing
 * without having to use duplicated code if it contains a {@link PatcherTransformer} method,
 * as those cannot be made a static method.
 */
public interface CommonTransformer extends PatcherTransformer {
    default void makeNametagTransparent(MethodNode methodNode) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        LabelNode afterDraw = new LabelNode();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                String nodeName = mapMethodNameFromNode(node);
                if (nodeName.equals("begin") || nodeName.equals("func_181668_a")) {
                    AbstractInsnNode prevNode = node.getPrevious().getPrevious().getPrevious();
                    methodNode.instructions.insertBefore(prevNode, new FieldInsnNode(Opcodes.GETSTATIC,
                        getPatcherConfigClass(),
                        "disableNametagBoxes",
                        "Z"));
                    methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, afterDraw));
                } else if (nodeName.equals("draw") || nodeName.equals("func_78381_a")) {
                    methodNode.instructions.insert(node, afterDraw);
                    break;
                }
            }
        }
    }

    default void makeNametagShadowed(MethodNode methodNode) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                String nodeName = mapMethodNameFromNode(node);
                if (nodeName.equals("drawString") || nodeName.equals("func_78276_b")) {
                    methodNode.instructions.set(node, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "club/sk1er/patcher/hooks/NameTagRenderingHooks",
                        "drawNametagText",
                        "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)I",
                        false));
                }
            }
        }
    }
}
