package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.asm.network.packet.C01PacketChatMessageTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
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
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "holdingShift", "Z", null, null));
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "initGui":
                case "func_73866_w_": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.PUTFIELD) {
                            String fieldName = mapFieldNameFromNode(next);

                            if (fieldName.equals("sentHistoryCursor") || fieldName.equals("field_146416_h")) {
                                methodNode.instructions.insertBefore(next.getNext(), createWasInitBefore());
                            }
                        } else if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(next);

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
                    break;
                }
                case "drawScreen":
                case "func_73863_a": {
                    LabelNode ifne = new LabelNode();
                    methodNode.instructions.insert(getOption(ifne));

                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode) {
                            if (next.getOpcode() == Opcodes.INVOKESTATIC) {
                                String methodInsnName = mapMethodNameFromNode(next);

                                if (methodInsnName.equals("drawRect") || methodInsnName.equals("func_73734_a")) {
                                    methodNode.instructions.insertBefore(next.getNext(), ifne);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }

                case "keyTyped":
                case "func_73869_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodNameInsn = mapMethodNameFromNode(next);

                            if (methodNameInsn.equals("displayGuiScreen") || methodNameInsn.equals("func_147108_a")) {
                                AbstractInsnNode unused = next;
                                for (int i = 0; i < 8; i++) {
                                    unused = unused.getPrevious();
                                }

                                boolean insert = unused instanceof MethodInsnNode && unused.getOpcode() == Opcodes.INVOKEVIRTUAL;

                                if (insert) {
                                    for (int i = 0; i < 4; i++) {
                                        next = next.getPrevious();
                                    }

                                    LabelNode gotoInsn = new LabelNode();
                                    methodNode.instructions.insertBefore(next, checkChat(gotoInsn));

                                    for (int i = 0; i < 4; i++) {
                                        next = next.getNext();
                                    }

                                    methodNode.instructions.insertBefore(next.getNext(), gotoInsn);
                                }
                            }
                        }
                    }

                    break;
                }

                case "func_73864_a":
                case "mouseClicked": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("handleComponentClick") || methodInsnName.equals("func_175276_a")) {
                                methodNode.instructions.insertBefore(next.getNext().getNext(),
                                    new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("GuiNewChatHook"), "mouseClicked", "()V", false));
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList checkChat(LabelNode gotoInsn) {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("shiftChat", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiChat", "holdingShift", "Z"));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiChat", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_147108_a", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiChat", "field_146415_a", "Lnet/minecraft/client/gui/GuiTextField;"));
        list.add(new LdcInsnNode(""));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/GuiTextField", "func_146180_a", "(Ljava/lang/String;)V", false));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        return list;
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
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        LabelNode ifeq = new LabelNode();
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
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/gui/GuiChat", "func_146272_n", "()Z", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/gui/GuiChat", "holdingShift", "Z"));
        list.add(getPatcherSetting("transparentChatInputField", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }
}
