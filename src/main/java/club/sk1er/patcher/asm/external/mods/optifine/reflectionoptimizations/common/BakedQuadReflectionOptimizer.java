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

package club.sk1er.patcher.asm.external.mods.optifine.reflectionoptimizations.common;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BakedQuadReflectionOptimizer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.block.model.BakedQuad"};
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
            if (methodNode.name.equals("pipe")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(pipeReflectionOptimization());
                break;
            }
        }
    }

    private InsnList pipeReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraftforge/client/model/pipeline/LightUtil",
            "putBakedQuad",
            "(Lnet/minecraftforge/client/model/pipeline/IVertexConsumer;Lnet/minecraft/client/renderer/block/model/BakedQuad;)V",
            false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
