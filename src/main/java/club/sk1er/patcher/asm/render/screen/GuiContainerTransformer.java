package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiContainerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.inventory.GuiContainer"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            switch (methodName) {
                case "mouseClicked":
                case "func_73864_a": {
                    final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            final String fieldName = mapFieldNameFromNode(next);
                            if ((fieldName.equals("touchscreen") || fieldName.equals("field_85185_A")) && next.getNext().getNext().getOpcode() == Opcodes.ILOAD) {
                                LabelNode ifne = new LabelNode();
                                method.instructions.insertBefore(next.getPrevious().getPrevious().getPrevious(), shouldActivateClick(ifne));
                                method.instructions.insertBefore(next.getNext().getNext(), ifne);
                            }
                        }
                    }

                    method.instructions.insert(method.instructions.getFirst().getNext().getNext().getNext(), checkCloseWindow());
                    method.instructions.insertBefore(method.instructions.getLast().getPrevious(), checkHotbarKeys());
                    break;
                }
                case "updateDragSplitting":
                case "func_146980_g": {
                    ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                    LabelNode gotoInsn = new LabelNode();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode) {
                            if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                String methodInsnName = mapMethodNameFromNode(next);

                                if (methodInsnName.equals("copy") || methodInsnName.equals("func_77946_l")) {
                                    method.instructions.insertBefore(next.getPrevious(), fixSplitRemnants(gotoInsn));
                                }
                            } else if (next.getOpcode() == Opcodes.INVOKEINTERFACE) {
                                if (((MethodInsnNode) next).name.equals("iterator")) {
                                    method.instructions.insertBefore(next.getNext().getNext(), gotoInsn);
                                }
                            }
                        }
                    }
                    break;
                }
                case "keyTyped":
                case "func_73869_a": {
                    final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("isCtrlKeyDown") || methodInsnName.equals("func_146271_m")) {
                                for (int i = 0; i < 7; i++) {
                                    method.instructions.remove(next.getNext());
                                }

                                method.instructions.insertBefore(next.getNext(), checkPatcherKey());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList shouldActivateClick(LabelNode ifne) {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("clickOutOfContainers", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        return list;
    }

    private InsnList checkPatcherKey() {
        InsnList list = new InsnList();
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/Patcher", "instance", "Lclub/sk1er/patcher/Patcher;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/Patcher", "getDropModifier", "()Lnet/minecraft/client/settings/KeyBinding;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/settings/GameSettings", "func_100015_a", "(Lnet/minecraft/client/settings/KeyBinding;)Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode _goto = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, _goto));
        list.add(ifeq);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(_goto);
        return list;
    }

    private InsnList fixSplitRemnants(LabelNode gotoInsn) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "field_146988_G", "I"));
        list.add(new InsnNode(Opcodes.ICONST_2));
        LabelNode ificmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, ificmpne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77976_d", "()I", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "field_146996_I", "I"));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ificmpne);
        return list;
    }

    private InsnList checkHotbarKeys() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new VarInsnNode(Opcodes.BIPUSH, 100));
        list.add(new InsnNode(Opcodes.ISUB));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/inventory/GuiContainer", "func_146983_a", "(I)Z", false));
        list.add(new InsnNode(Opcodes.POP));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList checkCloseWindow() {
        InsnList list = new InsnList();
        LabelNode labelNode = new LabelNode();
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new VarInsnNode(Opcodes.BIPUSH, 100));
        list.add(new InsnNode(Opcodes.ISUB));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71474_y", "Lnet/minecraft/client/settings/GameSettings;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/settings/GameSettings", "field_151445_Q", "Lnet/minecraft/client/settings/KeyBinding;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/settings/KeyBinding", "func_151463_i", "()I", false));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "field_146297_k", "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", "func_71053_j", "()V", false));
        list.add(labelNode);
        return list;
    }
}
