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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ChunkTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.chunk.Chunk"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        List<String> brightness = Arrays.asList(
            "getLightFor", "func_177413_a",
            "getLightSubtracted", "func_177443_a"
        );

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            if (brightness.contains(methodName)) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), setLightLevel());
            } else if (methodName.equals("setBlockState") || methodName.equals("func_177436_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                        String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                        // remove the + 1 from relightBlock(x, y + 1, z);
                        if ((methodInsnName.equals("relightBlock") || methodInsnName.equals("func_76615_h")) && next.getPrevious().getPrevious().getOpcode() == Opcodes.IADD) {
                            methodNode.instructions.remove(next.getPrevious().getPrevious());
                            methodNode.instructions.remove(next.getPrevious().getPrevious());
                            break;
                        }
                    }
                }
            } else if (methodName.equals("getBlockState") || methodName.equals("func_177435_g")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getBlockStateFast());
            }
        }
    }

    private InsnList getBlockStateFast() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177958_n", "()I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177956_o", "()I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/BlockPos", "func_177952_p", "()I", false));
        list.add(new VarInsnNode(Opcodes.ISTORE, 4));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFLT, labelNode));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new InsnNode(Opcodes.ICONST_4));
        list.add(new InsnNode(Opcodes.ISHR));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/chunk/Chunk", "field_76652_q", "[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"));
        list.add(new InsnNode(Opcodes.ARRAYLENGTH));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/chunk/Chunk", "field_76652_q", "[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new InsnNode(Opcodes.ICONST_4));
        list.add(new InsnNode(Opcodes.ISHR));
        list.add(new InsnNode(Opcodes.AALOAD));
        list.add(new VarInsnNode(Opcodes.ASTORE, 5));
        list.add(new VarInsnNode(Opcodes.ALOAD, 5));
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        list.add(new InsnNode(Opcodes.IAND));
        list.add(new VarInsnNode(Opcodes.ISTORE, 6));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        list.add(new InsnNode(Opcodes.IAND));
        list.add(new VarInsnNode(Opcodes.ISTORE, 7));
        list.add(new VarInsnNode(Opcodes.ILOAD, 4));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        list.add(new InsnNode(Opcodes.IAND));
        list.add(new VarInsnNode(Opcodes.ISTORE, 8));
        list.add(new VarInsnNode(Opcodes.ALOAD, 5));
        list.add(new VarInsnNode(Opcodes.ILOAD, 6));
        list.add(new VarInsnNode(Opcodes.ILOAD, 7));
        list.add(new VarInsnNode(Opcodes.ILOAD, 8));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/storage/ExtendedBlockStorage", "func_177485_a", "(III)Lnet/minecraft/block/state/IBlockState;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(labelNode);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/init/Blocks", "field_150350_a", "Lnet/minecraft/block/Block;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", "func_176223_P", "()Lnet/minecraft/block/state/IBlockState;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList setLightLevel() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/FullbrightTicker", "isFullbright", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }
}
