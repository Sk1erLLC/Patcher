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

package club.sk1er.patcher.asm.external.lwjgl;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class WindowsKeycodesTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"org.lwjgl.opengl.WindowsKeycodes"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("mapVirtualKeyToLWJGLCode") && methodNode.desc.equals("(I)I")) {
                methodNode.instructions.insert(methodNode.instructions.getFirst().getNext(), correctCodes());
                break;
            }
        }
    }

    // int corrected = extraVirtualKeys(virtualKey);
    // if (corrected != virtualKey) return corrected;
    private InsnList correctCodes() {
        InsnList list = new InsnList();
        LabelNode labelNode = new LabelNode();

        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/keybind/KeycodeHelper", "extraVirtualKeysWindows", "(I)I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 1));

        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, labelNode));

        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new InsnNode(Opcodes.IRETURN));

        list.add(labelNode);
        return list;
    }
}
