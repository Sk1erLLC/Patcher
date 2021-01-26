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

import club.sk1er.patcher.Patcher;
import club.sk1er.patcher.tweaker.ClassTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class GuiOverlayDebugTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiOverlayDebug"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("getDebugInfoRight") || methodName.equals("func_175238_c")) {
                int listIndex = 9;

                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("addAll")) {
                        method.instructions.insertBefore(next.getNext(), insertPatcher(listIndex));
                        break;
                    }
                }
            } else {
                final String optifineVersion = ClassTransformer.optifineVersion;
                final boolean compatibleVersion = optifineVersion.equals("L5") || optifineVersion.startsWith("L6") || optifineVersion.startsWith("M5");
                if (compatibleVersion && methodName.equals("call")) {
                    ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals("/")) {
                            AbstractInsnNode prev = next;
                            for (int i = 0; i < 8; i++) prev = prev.getPrevious();
                            JumpInsnNode iflt = (JumpInsnNode) prev;
                            method.instructions.insert(iflt, jumpIfNormalFpsCounter(iflt.label));
                            break;
                        }
                    }
                }
            }
        }
    }

    private InsnList insertPatcher(int listIndex) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, listIndex));
        list.add(new LdcInsnNode("Patcher " + Patcher.VERSION));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }

    private InsnList jumpIfNormalFpsCounter(LabelNode labelNode) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "normalFpsCounter", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        return list;
    }
}
