package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiPlayerTabOverlayTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            if (methodName.equalsIgnoreCase("renderPlayerlist")) {//TODO mappings

                // todo: you kinda need a bossbar for a bossbar feature to be checked for huh? how about we uhhhhhh
                // todo: do that
                InsnList list = new InsnList();
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager",
                        "pushMatrix", "()V", false));
                list.add(new InsnNode(Opcodes.FCONST_0));
                list.add(new LdcInsnNode(12.0f));
                list.add(new InsnNode(Opcodes.FCONST_0));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager",
                        "translate", "(FFF)V", false));

                method.instructions.insertBefore(method.instructions.getFirst(), list);

                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "net/minecraft/client/renderer/GlStateManager", "popMatrix", "()V", false));

                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                outer:
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode) {
                        if (((LdcInsnNode) next).cst instanceof Integer) {
                            if ((Integer) ((LdcInsnNode) next).cst == 553648127) {
                                iterator.remove();

                                AbstractInsnNode insertBeforeThisNode = iterator.next();

                                AbstractInsnNode previous;
                                int adds = 0;
                                do {
                                    previous = iterator.previous();
                                    if (previous instanceof InsnNode && previous.getOpcode() == Opcodes.IADD) {
                                        adds++;
                                        if (adds == 1) continue;

                                        int i1 = ((VarInsnNode) previous.getPrevious()).var;
                                        int j2 = ((VarInsnNode) previous.getPrevious().getPrevious()).var;
                                        int k2 = ((VarInsnNode) previous.getPrevious().getPrevious().getPrevious()).var;

                                        InsnList insns = new InsnList();
                                        insns.add(new VarInsnNode(Opcodes.ILOAD, i1));
                                        insns.add(new VarInsnNode(Opcodes.ILOAD, j2));
                                        insns.add(new VarInsnNode(Opcodes.ILOAD, k2));
                                        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook", "getColor", "(III)I", false));
                                        method.instructions.insertBefore(insertBeforeThisNode, insns);
                                        break outer;
                                    }
                                } while (previous != null);

                            }
                        }
                    }
                }
            }
        }
    }
}
