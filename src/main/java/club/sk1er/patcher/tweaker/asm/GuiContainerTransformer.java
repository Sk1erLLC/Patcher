/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

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

            if (methodName.equals("mouseClicked") || methodName.equals("func_73864_a")) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), checkHotbarKeys());
            } else if (methodName.equals("updateDragSplitting") || methodName.equals("func_146980_g")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                LabelNode gotoInsn = new LabelNode();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode) {
                        if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

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
            }
        }
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
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "mouseBindFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));

        list.add(new VarInsnNode(Opcodes.BIPUSH, 100));
        list.add(new InsnNode(Opcodes.ISUB));

        list.add(
            new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/client/gui/inventory/GuiContainer",
                "func_146983_a",
                "(I)Z",
                false));
        list.add(new InsnNode(Opcodes.POP));
        list.add(ifeq);
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
