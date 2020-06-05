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

package club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ModelRotationReflectionOptimizer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.resources.model.ModelRotation"};
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
            if (methodNode.name.equals("apply")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(applyReflectionOptimization());
            } else if (methodNode.name.equals("getMatrix")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(getMatrixReflectionOptimization());
            }
        }
    }

    private InsnList getMatrixReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraftforge/client/ForgeHooksClient",
            "getMatrix",
            "(Lnet/minecraft/client/resources/model/ModelRotation;)Ljavax/vecmath/Matrix4f;",
            false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }

    private InsnList applyReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/resources/model/ModelRotation",
            "getMatrix",
            "()Ljavax/vecmath/Matrix4f;",
            false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraftforge/client/ForgeHooksClient",
            "applyTransform",
            "(Ljavax/vecmath/Matrix4f;Lcom/google/common/base/Optional;)Lcom/google/common/base/Optional;",
            false));
        list.add(new InsnNode(Opcodes.ARETURN));
        return list;
    }
}
