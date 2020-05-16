package club.sk1er.patcher.tweaker.asm.lwjgl;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class WindowsDisplayTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"org.lwjgl.opengl.WindowsDisplay"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("doHandleMessage")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                outer:
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next.getOpcode() == Opcodes.ICONST_3 && iterator.hasNext()) {
                        AbstractInsnNode following = iterator.next();
                        if (following.getOpcode() == Opcodes.ICONST_1) {
                            //we found it
                            AbstractInsnNode node = following;
                            LabelNode labelNode = null;
                            while (node.getPrevious() != null) {
                                if (node instanceof LabelNode)
                                    labelNode = (LabelNode) node;
                                node = node.getPrevious();
                                if (node instanceof JumpInsnNode && node.getOpcode() == Opcodes.IFNE && labelNode != null) {
                                    JumpInsnNode jump = (JumpInsnNode) node;
                                    LabelNode after = jump.label;
                                    jump.label = labelNode;
                                    jump.setOpcode(Opcodes.IFEQ);
                                    InsnList fix = new InsnList();
                                    fix.add(new VarInsnNode(Opcodes.LLOAD, 4));
                                    fix.add(new LdcInsnNode(255L));
                                    fix.add(new InsnNode(Opcodes.LAND));
                                    fix.add(new LdcInsnNode(36L));
                                    fix.add(new InsnNode(Opcodes.LCMP));
                                    fix.add(new JumpInsnNode(Opcodes.IFNE, after));
                                    method.instructions.insert(node, fix);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
