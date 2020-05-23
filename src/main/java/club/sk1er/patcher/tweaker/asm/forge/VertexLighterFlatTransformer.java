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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VertexLighterFlatTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.pipeline.VertexLighterFlat"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        MethodNode resetBlockInfo = new MethodNode(Opcodes.ACC_PUBLIC, "resetBlockInfo", "()V", null, null);
        resetBlockInfo.instructions.add(resetBlockInfoInstructions());
        classNode.methods.add(resetBlockInfo);
    }

    private InsnList resetBlockInfoInstructions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/VertexLighterFlat", "blockInfo", "Lnet/minecraftforge/client/model/pipeline/BlockInfo;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/model/pipeline/BlockInfo", "reset", "()V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
