package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class DataWatcherTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.DataWatcher"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            LabelNode lockIfeq = new LabelNode();
            LabelNode unlockIfeq = new LabelNode();
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("getWatchedObject") || methodName.equals("func_75691_i")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (((MethodInsnNode) next).name.equals("lock")) {
                            method.instructions.insert(checkRemote(lockIfeq));
                            method.instructions.insertBefore(next.getNext(), lockIfeq);
                        } else if (((MethodInsnNode) next).name.equals("unlock")) {
                            method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), checkRemote(unlockIfeq));
                            method.instructions.insertBefore(next.getNext(), unlockIfeq);
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList checkRemote(LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("DataWatcherHook"), "checkWorldStatus", "()Z", false));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        return list;
    }
}
