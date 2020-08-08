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

import java.util.ListIterator;

public class LayerArrowTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerArrow"};
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

            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESTATIC) {
                        String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);
                        if (methodInsnName.equals("disableStandardItemLighting") || methodInsnName.equals("func_74518_a")) {
                            methodNode.instructions.insertBefore(next, fixArrowLighting(true));
                            methodNode.instructions.remove(next);
                        } else if (methodInsnName.equals("enableStandardItemLighting") || methodInsnName.equals("func_74519_b")) {
                            methodNode.instructions.insertBefore(next, fixArrowLighting(false));
                            methodNode.instructions.remove(next);
                            break;
                        }
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), cancelRendering());
                break;
            }
        }
    }

    private InsnList fixArrowLighting(boolean disable) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "fixArrowLighting", "Z"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/RenderHelper", disable ? "func_74518_a" : "func_74519_b", "()V", false));
        list.add(labelNode);
        return list;
    }

    private InsnList cancelRendering() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "disableAttachedArrows", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
