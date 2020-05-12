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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ItemRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.ItemRenderer"};
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

            if (methodName.equals("renderWaterOverlayTexture") || methodName.equals("func_78448_c")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), removeOverlay());
            } else if (methodName.equals("renderFireInFirstPerson") || methodName.equals("func_78442_d")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), changeHeight());
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), popMatrix());
            }
        }
    }

    private InsnList removeOverlay() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeWaterOverlay", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList changeHeight() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179094_E", // pushMatrix
            "()V", false));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "fireHeight", "I"));
        list.add(new InsnNode(Opcodes.I2F));
        list.add(new LdcInsnNode(100F));
        list.add(new InsnNode(Opcodes.FDIV));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179109_b", // translate
            "(FFF)V", false));
        return list;
    }

    private InsnList popMatrix() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179121_F", // popMatrix
            "()V", false));
        return list;
    }
}
