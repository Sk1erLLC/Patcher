package club.sk1er.patcher.asm.external.mods.optifine;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class OptifineFontRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.FontRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("getCharWidthFloat")) {
                method.access = Opcodes.ACC_PUBLIC;

                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next.getOpcode() == Opcodes.IF_ICMPLE) {
                        AbstractInsnNode prev = next.getPrevious();
                        if (prev.getOpcode() == Opcodes.BIPUSH && ((IntInsnNode) prev).operand == 7) {
                            JumpInsnNode node = (JumpInsnNode) next;
                            method.instructions.insert(node, new JumpInsnNode(Opcodes.GOTO, node.label));
                            method.instructions.remove(node.getPrevious().getPrevious());
                            method.instructions.remove(node.getPrevious());
                            method.instructions.remove(node);
                            break;
                        }
                    }
                }

                break;
            }
        }
    }
}
