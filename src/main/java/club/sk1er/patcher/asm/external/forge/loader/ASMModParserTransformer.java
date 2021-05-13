package club.sk1er.patcher.asm.external.forge.loader;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class ASMModParserTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.fml.common.discovery.asm.ASMModParser"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("toString")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD && ((FieldInsnNode) next).name.equals("baseModProperties")) {
                        next = next.getNext().getNext().getNext();
                        for (int i = 0; i < 12; i++) {
                            method.instructions.remove(next.getPrevious());
                        }

                        method.instructions.remove(next);
                        break;
                    }
                }

                break;
            }
        }
    }
}
