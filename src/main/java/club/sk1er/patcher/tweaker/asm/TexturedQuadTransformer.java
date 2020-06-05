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
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TexturedQuadTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.model.TexturedQuad"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (FieldNode fieldNode : classNode.fields) {
            if (fieldNode.name.equals("texturedQuadHook")) {
                System.out.println("User has Frames+, not using single model render call system.");
                return;
            }
        }

        System.out.println("Creating TexturedQuad#patcherTexturedQuad.");
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
            "patcherTexturedQuad",
            "Lclub/sk1er/patcher/hooks/TexturedQuadHook;",
            null,
            null));

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("draw") || methodName.equals("func_178765_a")) {
                clearInstructions(methodNode);
                methodNode.instructions.add(singleModelRenderCall());
            } else if (methodNode.name.equals("<init>")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), initTexturedQuadHook());
            }
        }
    }

    private InsnList initTexturedQuadHook() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, "club/sk1er/patcher/hooks/TexturedQuadHook"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "club/sk1er/patcher/hooks/TexturedQuadHook", "<init>", "(Lnet/minecraft/client/model/TexturedQuad;)V", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/TexturedQuad", "patcherTexturedQuad", "Lclub/sk1er/patcher/hooks/TexturedQuadHook;"));
        return list;
    }

    private InsnList singleModelRenderCall() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/model/TexturedQuad", "patcherTexturedQuad", "Lclub/sk1er/patcher/hooks/TexturedQuadHook;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.FLOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/patcher/hooks/TexturedQuadHook", "draw", "(Lnet/minecraft/client/renderer/WorldRenderer;F)V", false));
        list.add(new InsnNode(Opcodes.RETURN));
        return list;
    }
}
