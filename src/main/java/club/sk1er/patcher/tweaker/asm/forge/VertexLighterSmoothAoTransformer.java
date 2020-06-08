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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VertexLighterSmoothAoTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo"};
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
            if (methodNode.name.equals("calcLightmap")) {
                clearInstructions(methodNode);
                methodNode.desc = "([FFFF)V";
                methodNode.instructions.insert(fastLightmapCalc());
            } else if (methodNode.name.equals("updateLightmap")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(runFasterLightmap());
            }
        }
    }

    private InsnList fastLightmapCalc() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 2));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new VarInsnNode(Opcodes.FLOAD, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/hooks/VertexLighterSmoothAoHook", "fastCalcLightmap", "(Lnet/minecraftforge/client/model/pipeline/VertexLighterFlat;[FFFF)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }

    private InsnList runFasterLightmap() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.FLOAD, 3));
        list.add(new VarInsnNode(Opcodes.FLOAD, 4));
        list.add(new VarInsnNode(Opcodes.FLOAD, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/model/pipeline/VertexLighterSmoothAo", "calcLightmap", "([FFFF)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
