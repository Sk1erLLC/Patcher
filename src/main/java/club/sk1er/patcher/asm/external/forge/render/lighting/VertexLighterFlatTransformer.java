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

package club.sk1er.patcher.asm.external.forge.render.lighting;

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
        classNode.interfaces.add(getHookClass("accessors/IVertexLighterFlat"));

        MethodNode getBlockInfo = new MethodNode(Opcodes.ACC_PUBLIC, "getBlockInfo", "()Lnet/minecraftforge/client/model/pipeline/BlockInfo;", null, null);
        getBlockInfo.instructions.add(getBlockInfoInstructions());
        classNode.methods.add(getBlockInfo);

        MethodNode resetBlockInfo = new MethodNode(Opcodes.ACC_PUBLIC, "resetBlockInfo", "()V", null, null);
        resetBlockInfo.instructions.add(resetBlockInfoInstructions());
        classNode.methods.add(resetBlockInfo);

        for (MethodNode method : classNode.methods) {
            if (method.name.equals("updateBlockInfo")) {
                method.instructions.insertBefore(method.instructions.getLast().getPrevious(), updateFlatLighting());
            } else if (method.name.equals("updateLightmap")) {
                clearInstructions(method);
                method.instructions.insert(updateLightmap());
            }
        }
    }

    private InsnList updateLightmap() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/VertexLighterFlat", "blockInfo", "Lnet/minecraftforge/client/model/pipeline/BlockInfo;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new VarInsnNode(Opcodes.FLOAD, 4));
        list.add(new VarInsnNode(Opcodes.FLOAD, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("BlockInfoHook"), "updateLightmap", "(Lnet/minecraftforge/client/model/pipeline/BlockInfo;[F[FFFF)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList updateFlatLighting() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/VertexLighterFlat", "blockInfo", "Lnet/minecraftforge/client/model/pipeline/BlockInfo;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/model/pipeline/BlockInfo", "updateFlatLighting", "()V", false));
        return list;
    }

    private InsnList getBlockInfoInstructions() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/model/pipeline/VertexLighterFlat", "blockInfo", "Lnet/minecraftforge/client/model/pipeline/BlockInfo;"));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
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
