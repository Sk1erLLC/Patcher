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
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
                int listIndex = -1;

                for (LocalVariableNode variable : method.localVariables) {
                    listIndex = variable.name.equals("list") ? variable.index : 9; // bruh-sound-effect-2.mp3
                    break;
                }

                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), insertPatcher(listIndex));
                break;
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
}
