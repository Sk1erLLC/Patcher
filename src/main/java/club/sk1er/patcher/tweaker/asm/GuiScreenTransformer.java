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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiScreenTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreen"};
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

            switch (methodName) {
                case "drawDefaultBackground":
                case "func_146276_q_":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), cancelBackgroundRendering());
                    break;
                case "handleKeyboardInput":
                case "func_146282_l":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(handleForeignKeyboards());
                    break;
                case "handleInput":
                case "func_146269_k":
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("handleKeyboardInput") || methodInsnName.equals("func_146282_l")) {
                                methodNode.instructions.insertBefore(next.getPrevious(), bailScreen());
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    private InsnList bailScreen() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiScreen",
            "field_146297_k", // mc
            "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/Minecraft",
            "field_71462_r", // currentScreen
            "Lnet/minecraft/client/gui/GuiScreen;"));
        LabelNode ifacmpeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, ifacmpeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifacmpeq);
        return list;
    }

    private InsnList handleForeignKeyboards() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/input/Keyboard", "getEventCharacter", "()C", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/input/Keyboard", "getEventKey", "()I", false));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 32));
        LabelNode ificmpge = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPGE, ificmpge));
        list.add(ifne);
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/input/Keyboard", "getEventKeyState", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ificmpge);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/input/Keyboard", "getEventKey", "()I", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/gui/GuiScreen",
            "func_73869_a", // keyTyped
            "(CI)V",
            false));
        list.add(ifeq);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/client/gui/GuiScreen",
            "field_146297_k", // mc
            "Lnet/minecraft/client/Minecraft;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/Minecraft",
            "func_152348_aa", // dispatchKeypresses
            "()V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList cancelBackgroundRendering() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiScreen", "field_146297_k", // mc
            "Lnet/minecraft/client/Minecraft;"));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71441_e", // theWorld
            "Lnet/minecraft/client/multiplayer/WorldClient;"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "disableTransparentBackgrounds", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(labelNode);
        return list;
    }
}
