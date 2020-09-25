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
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class NBTTagStringTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.nbt.NBTTagString"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "dataCache", "Ljava/lang/String;", null, null));
        for (MethodNode method : classNode.methods) {
            final String s = mapMethodName(classNode, method);
            if (s.equals("read") || s.equals("func_152446_a")) {
                final InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new InsnNode(Opcodes.ACONST_NULL));
                insns.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/nbt/NBTTagString", "dataCache", "Ljava/lang/String;"));
                method.instructions.insertBefore(method.instructions.getFirst(), insns);
            } else if (s.equals("toString")) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/nbt/NBTTagString", "dataCache", "Ljava/lang/String;"));
                final LabelNode labelNode = new LabelNode();
                list.add(new JumpInsnNode(Opcodes.IFNONNULL, labelNode));
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(method.instructions.getFirst(), list);
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next.getOpcode() == Opcodes.ARETURN) {
                        final InsnList list1 = new InsnList();
                        list1.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/nbt/NBTTagString", "dataCache", "Ljava/lang/String;"));
                        list1.add(labelNode);
                        list1.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/nbt/NBTTagString", "dataCache", "Ljava/lang/String;"));
                        method.instructions.insertBefore(next, list1);
                    }
                }

            }
        }
    }
}
