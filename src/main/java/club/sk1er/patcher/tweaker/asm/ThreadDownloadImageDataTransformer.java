package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ThreadDownloadImageDataTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ThreadDownloadImageData"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("loadTexture") || methodName.equals("func_110551_a")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                        final String methodInsnName = mapMethodNameFromNode(next);

                        if (methodInsnName.equals("loadTexture") || methodInsnName.equals("func_110551_a")) {
                            method.instructions.insertBefore(next.getNext().getNext(), getImprovedCacheLoading());
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private InsnList getImprovedCacheLoading() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage() + "ThreadDownloadImageDataHook", "getImprovedCacheLoading", "(Lnet/minecraft/client/renderer/ThreadDownloadImageData;)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
