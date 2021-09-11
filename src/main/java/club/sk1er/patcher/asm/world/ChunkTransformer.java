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

package club.sk1er.patcher.asm.world;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);
            switch (methodName) {
                case "setBlockState":
                case "func_177436_a":
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                            String methodInsnName = mapMethodNameFromNode(next);

                            // remove the + 1 from relightBlock(x, y + 1, z);
                            if ((methodInsnName.equals("relightBlock") || methodInsnName.equals("func_76615_h")) && next.getPrevious().getPrevious().getOpcode() == Opcodes.IADD) {
                                methodNode.instructions.remove(next.getPrevious().getPrevious());
                                methodNode.instructions.remove(next.getPrevious().getPrevious());
                                break;
                            }
                        }
                    }
                    break;

                case "getBlockState":
                case "func_177435_g":
                    clearInstructions(methodNode);
                    methodNode.instructions.insert(getBlockStateFast());
                    break;
            }
        }
    }


    private InsnList getBlockStateFast() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("ChunkHook"), "getBlockState",
            "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
