package club.sk1er.patcher.asm.external.lwjgl;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class KeyboardTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"org.lwjgl.input.Keyboard"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("getKeyName")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.ARETURN) {
                        methodNode.instructions.insertBefore(node, getKeyName());
                        break;
                    }
                }
            }

            break;
        }
    }

    private InsnList getKeyName() { // if LWJGL doesnt find name for key, it'll ask KeycodeHelper
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ASTORE, 1));

        LabelNode labelnode = new LabelNode();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new JumpInsnNode(Opcodes.IFNONNULL, labelnode));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/keybind/KeycodeHelper", "getKeyName", "(I)Ljava/lang/String;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(labelnode);

        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        return list;
    }
}
