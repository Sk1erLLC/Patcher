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

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class EntityTransformer implements CommonTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.entity.Entity"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "displayNameCacheTime", "J", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "displayNameCache", "Lnet/minecraft/util/IChatComponent;", null, null));
        for (MethodNode methodNode : classNode.methods) {
            final String methodName = mapMethodName(classNode, methodNode);
            if (methodNode.name.equals("hasCapability")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(fasterCapabilityCheck());
            }

            switch (methodName) {
                case "getBrightnessForRender":
                case "func_70070_b":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(getFixedBrightness());
                    break;
                case "spawnRunningParticles":
                case "func_174830_Y":
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    LabelNode ifeq = new LabelNode();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof JumpInsnNode && next.getOpcode() == Opcodes.IFNE) {
                            methodNode.instructions.insert(next.getNext(), checkOnGround(ifeq));
                        } else if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("createRunningParticles") || methodInsnName.equals("func_174808_Z")) {
                                methodNode.instructions.insert(next.getNext(), ifeq);
                            }
                        }
                    }
                    break;
                case "getDisplayName":
                case "func_145748_c_":
                    cachePlayerHoverEvents(methodNode);
                    break;
            }
        }
    }

    private InsnList checkOnGround(LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "field_70122_E", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        return list;
    }

    private InsnList getFixedBrightness() {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/BlockPos"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/entity/Entity",
            "field_70165_t", // posX
            "D"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/entity/Entity",
            "field_70163_u", // posY
            "D"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/entity/Entity",
            "func_70047_e", // getEyeHeight
            "()F",
            false));
        list.add(new InsnNode(Opcodes.F2D));
        list.add(new InsnNode(Opcodes.DADD));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/entity/Entity",
            "field_70161_v", // posZ
            "D"));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/BlockPos", "<init>", "(DDD)V", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/entity/Entity",
            "field_70170_p", // worldObj
            "Lnet/minecraft/world/World;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/world/World",
            "func_175626_b", // getCombinedLight
            "(Lnet/minecraft/util/BlockPos;I)I",
            false));
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }

    private InsnList fasterCapabilityCheck() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "capabilities", "Lnet/minecraftforge/common/capabilities/CapabilityDispatcher;"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "capabilities", "Lnet/minecraftforge/common/capabilities/CapabilityDispatcher;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraftforge/common/capabilities/CapabilityDispatcher",
            "hasCapability",
            "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Z",
            false));
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new InsnNode(Opcodes.ICONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(labelNode);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }
}
