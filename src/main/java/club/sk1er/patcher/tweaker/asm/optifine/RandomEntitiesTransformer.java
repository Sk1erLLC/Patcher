package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class RandomEntitiesTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.optifine.RandomEntities"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<clinit>")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) next).name.equals("getFieldValue")) {
                        final AbstractInsnNode previous = next.getPrevious();

                        if (previous.getOpcode() == Opcodes.ICONST_0 || previous.getOpcode() == Opcodes.ICONST_1) {
                            method.instructions.insertBefore(previous, new InsnNode(
                                previous.getOpcode() == Opcodes.ICONST_0 ? Opcodes.ICONST_2 : Opcodes.ICONST_3)
                            );

                            method.instructions.remove(previous);
                        }
                    }
                }
            }
        }
    }
}
