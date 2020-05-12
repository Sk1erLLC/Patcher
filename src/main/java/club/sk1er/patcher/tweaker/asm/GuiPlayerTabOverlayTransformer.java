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

import java.util.Iterator;

public class GuiPlayerTabOverlayTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            if (methodName.equals("renderPlayerlist") || methodName.equals("func_175249_a")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        String currName = mapMethodNameFromNode((MethodInsnNode) node);
                        if (currName.equals("drawRect") || currName.equals("func_73734_a")) {
                            method.instructions.insertBefore(node, new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                                "getNewColor",
                                "(I)I",
                                false));
                        }
                    }
                }

                method.instructions.insertBefore(
                    method.instructions.getFirst(),
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                        "moveTabDownPushMatrix",
                        "()V",
                        false));
                method.instructions.insertBefore(
                    method.instructions.getLast().getPrevious(),
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                        "moveTabDownPopMatrix",
                        "()V",
                        false));
            } else if (methodName.equals("drawPing") || methodName.equals("func_175245_a")) {
                method.instructions.insertBefore(method.instructions.getFirst(), drawPatcherPing());
            }
        }
    }

    private InsnList drawPatcherPing() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "numberPing", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(
            new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "club/sk1er/patcher/hooks/GuiPlayerTabOverlayHook",
                "drawPatcherPing",
                "(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V",
                false));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
