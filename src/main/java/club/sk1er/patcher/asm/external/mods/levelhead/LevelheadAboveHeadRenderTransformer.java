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

package club.sk1er.patcher.asm.external.mods.levelhead;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class LevelheadAboveHeadRenderTransformer implements CommonTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"club.sk1er.mods.levelhead.renderer.LevelheadAboveHeadRender"};
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
            if (methodNode.name.equals("renderName")) {
                methodNode.instructions.insert(modifyNametagRenderState(true));
                makeNametagTransparent(methodNode);
            } else if (methodNode.name.equals("render")) {
                makeNametagShadowed(methodNode);
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof InsnNode && next.getOpcode() == Opcodes.DCONST_0) {
                        final LabelNode gotoInsn = new LabelNode();
                        methodNode.instructions.insertBefore(next, moveNametag(gotoInsn));
                        methodNode.instructions.insertBefore(next.getNext(), gotoInsn);
                        break;
                    }
                }
            }
        }
    }

    private InsnList moveNametag(LabelNode gotoInsn) {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("showOwnNametag", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new LdcInsnNode(0.3D));
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        return list;
    }
}
