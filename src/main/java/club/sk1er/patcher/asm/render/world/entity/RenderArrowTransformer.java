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

package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderArrowTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderArrow"};
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

            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                methodNode.instructions.insert(cancelRendering());
                break;
            }
        }
    }

    private InsnList cancelRendering() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("disableMovingArrows", "Z"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityArrow", "field_70254_i", "Z")); // inGround
        LabelNode labelNode1 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityArrow", "field_70159_w", "D")); // motionX
        list.add(new InsnNode(Opcodes.DCONST_0));
        list.add(new InsnNode(Opcodes.DCMPL));
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(labelNode);
        list.add(labelNode1);
        list.add(getPatcherSetting("disableGroundedArrows", "Z"));
        LabelNode ifeq2 = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityArrow", "field_70254_i", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq2));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq2);
        return list;
    }
}
