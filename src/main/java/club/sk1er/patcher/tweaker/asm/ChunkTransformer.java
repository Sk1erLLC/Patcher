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
            }
        }
    }

    private InsnList setLightLevel() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "fullbright", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 15));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }
}
