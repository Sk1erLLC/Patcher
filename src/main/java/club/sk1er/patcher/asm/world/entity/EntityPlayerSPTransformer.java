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

package club.sk1er.patcher.asm.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityPlayerSPTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.entity.EntityPlayerSP"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode removePotionEffectClient = new MethodNode(Opcodes.ACC_PUBLIC, "removePotionEffectClient", "(I)V", null, null);
        removePotionEffectClient.instructions.add(removePotionEffectClientMethod());
        classNode.methods.add(removePotionEffectClient);
    }

    private InsnList removePotionEffectClientMethod() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(getPatcherSetting("nauseaEffect", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/potion/Potion", "field_76431_k", "Lnet/minecraft/potion/Potion;")); // confusion
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/Potion", "field_76415_H", "I")); // id
        LabelNode ificmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, ificmpne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/entity/EntityPlayerSP", "field_71080_cy", "F")); // prevTimeInPortal
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/entity/EntityPlayerSP", "field_71086_bY", "F")); // timeInPortal
        list.add(ifeq);
        list.add(ificmpne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer", "func_70618_n", // removePotionEffectClient
            "(I)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
