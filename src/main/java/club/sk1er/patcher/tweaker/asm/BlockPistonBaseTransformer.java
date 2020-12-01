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
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class BlockPistonBaseTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.block.BlockPistonBase"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
            "directions",
            "[Lnet/minecraft/util/EnumFacing;",
            null,
            null));

        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (method.name.equals("<clinit>")) {
                method.instructions.insertBefore(method.instructions.getFirst(), createDirections());
            } else if (methodName.equals("shouldBeExtended") || methodName.equals("func_176318_b")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("values")) {
                        method.instructions.insertBefore(next, new FieldInsnNode(Opcodes.GETSTATIC,
                            "net/minecraft/block/BlockPistonBase",
                            "directions",
                            "[Lnet/minecraft/util/EnumFacing;"));
                        method.instructions.remove(next);
                    }
                }
            }
        }
    }

    private InsnList createDirections() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/EnumFacing", "values", "()[Lnet/minecraft/util/EnumFacing;", false));
        list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraft/block/BlockPistonBase", "directions", "[Lnet/minecraft/util/EnumFacing;"));
        return list;
    }
}
