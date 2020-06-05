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

package club.sk1er.patcher.tweaker.asm.forge;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BlockInfoTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.pipeline.BlockInfo"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode reset = new MethodNode(Opcodes.ACC_PUBLIC, "reset", "()V", null, null);
        reset.instructions.add(resetInstructions());
        classNode.methods.add(reset);
    }

    private InsnList resetInstructions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "world", "Lnet/minecraft/world/IBlockAccess;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ACONST_NULL));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "blockPos", "Lnet/minecraft/util/BlockPos;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "cachedTint", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_M1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "cachedMultiplier", "I"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.FCONST_0));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shz", "F"));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shy", "F"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/client/model/pipeline/BlockInfo", "shx", "F"));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
