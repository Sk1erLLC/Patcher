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
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class ModelRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.model.ModelRenderer"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "patcherCompiledState", "Z", null, null));
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("render") || methodName.equals("func_78785_a")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), renderStart());
            } else if (methodName.equals("compileDisplayList") || methodName.equals("func_78788_d")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof VarInsnNode) {
                        if (next.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) next).var == 2) {
                            methodNode.instructions.insert(next, getWorldRendererBegin());
                        }
                    } else if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("glEndList")) {
                        methodNode.instructions.insertBefore(next, getWorldRendererEnd());
                    }
                }
            }
        }
    }

    public InsnList getWorldRendererEnd() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "singleModelCall", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraft/client/renderer/Tessellator",
            "func_178181_a", // getInstance
            "()Lnet/minecraft/client/renderer/Tessellator;",
            false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/renderer/Tessellator",
            "func_78381_a", // draw
            "()V",
            false));
        list.add(ifeq);
        return list;
    }

    private InsnList getWorldRendererBegin() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "singleModelCall", "Z"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelRenderer", "patcherCompiledState", "Z"));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "singleModelCall", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new IntInsnNode(Opcodes.BIPUSH, 7));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC,
            "net/minecraft/client/renderer/vertex/DefaultVertexFormats",
            "field_181703_c", // OLDMODEL_POSITION_TEX_NORMAL
            "Lnet/minecraft/client/renderer/vertex/VertexFormat;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "net/minecraft/client/renderer/WorldRenderer",
            "func_181668_a", // begin
            "(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V",
            false));
        list.add(ifeq);
        return list;
    }

    private InsnList renderStart() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/model/ModelRenderer", "patcherCompiledState", "Z"));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "singleModelCall", "Z"));
        LabelNode ificmpeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, ificmpeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD,
            "net/minecraft/client/model/ModelRenderer",
            "field_78812_q", // compiled
            "Z"));
        list.add(ificmpeq);
        return list;
    }
}
