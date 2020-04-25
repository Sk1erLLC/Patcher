package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiChatTransformer implements PatcherTransformer {

    public static int maxChatLength = 100;

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("initGui") || methodName.equals("func_73866_w_")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.PUTFIELD) {
                        String fieldName = mapFieldNameFromNode((FieldInsnNode) next);

                        if (fieldName.equals("sentHistoryCursor") || fieldName.equals("field_146416_h")) {
                            methodNode.instructions.insertBefore(next.getNext(), createWasInitBefore());
                        }
                    } else if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                        if (methodInsnName.equals("setText") || methodInsnName.equals("func_175274_a")) {
                            for (int i = 0; i < 4; ++i) {
                                methodNode.instructions.remove(next.getPrevious());
                            }

                            methodNode.instructions.remove(next);
                            break;
                        }
                    }
                }

                C01PacketChatMessageTransformer.extendChatLength(methodNode);
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), setText());
            } else if (methodName.equals("drawScreen") || methodName.equals("func_73863_a")) {
                LabelNode ifne = new LabelNode();
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getOption(ifne));

                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                            if (methodInsnName.equals("drawRect") || methodInsnName.equals("func_73734_a")) {
                                methodNode.instructions.insertBefore(next.getNext(), ifne);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private InsnList setText() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiChat",
            "field_146415_a", // inputField
            "Lnet/minecraft/client/gui/GuiTextField;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiChat",
            "field_146409_v", // defaultInputFieldText
            "Ljava/lang/String;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/gui/GuiTextField",
            "func_146180_a", // setText
            "(Ljava/lang/String;)V",
            false));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiChat",
            "field_146415_a", // inputField
            "Lnet/minecraft/client/gui/GuiTextField;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/gui/GuiTextField",
            "func_146180_a", // setText
            "(Ljava/lang/String;)V",
            false));
        list.add(gotoInsn);
        return list;
    }

    private InsnList createWasInitBefore() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiChat",
            "field_146415_a", // inputField
            "Lnet/minecraft/client/gui/GuiTextField;"));
        LabelNode ifnull = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, ifnull));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifnull);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        list.add(new VarInsnNode(Opcodes.ISTORE, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "chatKeeper", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiChat",
            "field_146415_a", // inputField
            "Lnet/minecraft/client/gui/GuiTextField;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/gui/GuiTextField",
            "func_146179_b", // getText
            "()Ljava/lang/String;",
            false));
        LabelNode gotoInsn2 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn2));
        list.add(ifeq);
        list.add(new LdcInsnNode(""));
        list.add(gotoInsn2);
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        return list;
    }

    private InsnList getOption(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentChatInputField", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }
}
