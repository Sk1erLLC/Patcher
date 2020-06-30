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
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class EntityTransformer implements PatcherTransformer {
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
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodNode.name.equals("hasCapability")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(fasterCapabilityCheck());
            } else if (methodName.equals("getBrightnessForRender") || methodName.equals("func_70070_b")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getFixedBrightness());
            } else if (methodName.equalsIgnoreCase("func_145775_I") || methodName.equalsIgnoreCase("doBlockCollisions")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                int it = 0;
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof TypeInsnNode) {
                        if (FMLDeobfuscatingRemapper.INSTANCE.map(((TypeInsnNode) next).desc).equalsIgnoreCase("net/minecraft/util/BlockPos")) {
                            it++;
                            int var = 0;
                            if (it == 3) {
                                AbstractInsnNode tmp;
                                int steps = 0;
                                while ((tmp = iterator.next()) != null) {
                                    steps++;
                                    if (tmp.getOpcode() == Opcodes.ASTORE) {
                                        var = ((VarInsnNode) tmp).var;
                                        iterator.remove();
                                        break;
                                    }
                                }
                                for (int i = 0; i < steps ; i++) {
                                    iterator.previous(); //Rewind
                                }
                                iterator.remove();
                                iterator.next();
                                iterator.remove();
                                methodNode.instructions.insertBefore(iterator.next(), new VarInsnNode(Opcodes.ALOAD, var));
                                while ((tmp = iterator.next()) != null) {
                                    if (tmp.getOpcode() == Opcodes.INVOKESPECIAL) {
                                        iterator.remove();
                                        methodNode.instructions.insertBefore(iterator.next(), new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/util/world/entity/culling/BetterBlockPos", "update", "(III)V", false));
                                        break;
                                    }
                                }
                                final InsnList insns = new InsnList();
                                insns.add(new TypeInsnNode(Opcodes.NEW, "club/sk1er/patcher/util/world/entity/culling/BetterBlockPos"));
                                insns.add(new InsnNode(Opcodes.DUP));
                                insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "club/sk1er/patcher/util/world/entity/culling/BetterBlockPos", "<init>", "()V", false));
                                insns.add(new VarInsnNode(Opcodes.ASTORE, var));
                                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insns);

                            }
                        }
                    }
                }
            }
        }

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
