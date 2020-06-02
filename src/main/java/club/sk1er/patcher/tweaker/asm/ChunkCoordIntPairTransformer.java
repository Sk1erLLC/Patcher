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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ChunkCoordIntPairTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.ChunkCoordIntPair"};
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

            if (methodName.equals("hashCode")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getFasterHashCode());
                break;
            }
        }
    }

    private InsnList getFasterHashCode() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/world/ChunkCoordIntPair",
            "field_77276_a", // chunkXPos
            "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD,
            "net/minecraft/world/ChunkCoordIntPair",
            "field_77275_b", // chunkZPos
            "I"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraft/world/ChunkCoordIntPair",
            "func_77272_a", // chunkXZ2Int
            "(II)J",
            false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/hash/FastHashedKey", "getFasterHashedKey", "(J)I", false));
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }
}
