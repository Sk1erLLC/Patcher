package club.sk1er.patcher.asm.network;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class NetHandlerPlayServerTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.network.NetHandlerPlayServer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("processVanilla250Packet") || methodName.equals("func_147349_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodInsnName = mapMethodNameFromNode(next);
                        if (methodInsnName.equals("release")) {
                            LabelNode ifeq = new LabelNode();
                            method.instructions.insertBefore(next.getPrevious(), NetHandlerPlayClientTransformer.createList(ifeq));
                            method.instructions.insertBefore(next.getNext().getNext(), ifeq);
                        }
                    }
                }
            }
        }
    }
}
