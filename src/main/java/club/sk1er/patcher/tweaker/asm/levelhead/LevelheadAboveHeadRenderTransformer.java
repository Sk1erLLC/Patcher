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

package club.sk1er.patcher.tweaker.asm.levelhead;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class LevelheadAboveHeadRenderTransformer implements PatcherTransformer {

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
                makeNametagTransparent(methodNode);
            } else if (methodNode.name.equals("render")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof InsnNode && next.getOpcode() == Opcodes.DCONST_0) {
                        LabelNode gotoInsn = new LabelNode();
                        methodNode.instructions.insertBefore(next, moveNametag(gotoInsn));
                        methodNode.instructions.insertBefore(next.getNext().getNext(), gotoInsn);
                        break;
                    }
                }
            }
        }
    }

    private InsnList moveNametag(LabelNode gotoInsn) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "showOwnNametag", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new LdcInsnNode(0.3D));
        list.add(new VarInsnNode(Opcodes.DSTORE, 9));
        list.add(ifeq);
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        return list;
    }

    private void makeNametagTransparent(MethodNode methodNode) {
        ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        LabelNode afterDraw = new LabelNode();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                String nodeName = mapMethodNameFromNode((MethodInsnNode) node);
                if (nodeName.equals("begin") || nodeName.equals("func_181668_a")) {
                    AbstractInsnNode prevNode = node.getPrevious().getPrevious().getPrevious();
                    methodNode.instructions.insertBefore(prevNode,
                        new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentNameTags",
                            "Z"));
                    methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, afterDraw));
                } else if (nodeName.equals("draw") || nodeName.equals("func_78381_a")) {
                    methodNode.instructions.insert(node, afterDraw);
                    break;
                }
            }
        }
    }
}
