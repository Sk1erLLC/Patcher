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

package club.sk1er.patcher.tweaker.asm.lwjgl;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class WindowsDisplayTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"org.lwjgl.opengl.WindowsDisplay"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("doHandleMessage")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next.getOpcode() == Opcodes.ICONST_3 && iterator.hasNext()) {
                        AbstractInsnNode following = iterator.next();
                        if (following.getOpcode() == Opcodes.ICONST_1) {
                            //we found it
                            AbstractInsnNode node;
                            while (true) {
                                node = iterator.previous();
                                if (node instanceof JumpInsnNode && node.getOpcode() == Opcodes.IFNE) {
                                    node = iterator.previous();
                                    for (int i = 0; i < 3; i++) {
                                        AbstractInsnNode previous = iterator.previous();
                                        iterator.remove();
                                    }
                                    InsnList fix = new InsnList();
                                    fix.add(new IntInsnNode(Opcodes.BIPUSH, 16));
                                    fix.add(new InsnNode(Opcodes.LSHR));
                                    fix.add(new InsnNode(Opcodes.LCONST_1));
                                    method.instructions.insertBefore(node, fix);
                                    return;
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
