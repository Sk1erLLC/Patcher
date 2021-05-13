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

package club.sk1er.patcher.asm.render.item;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderItemTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderItem"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
            "patcherRenderItemHook",
            "L" + getHookClass("RenderItemHook;"),
            null,
            null));

        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodNode.name.equals("<init>")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), renderItemHookInit());
            } else if (methodName.equals("renderEffect") || methodName.equals("func_180451_a")) {
                methodNode.instructions.insert(cancelRendering());
            } else if ((methodName.equals("renderModel") || methodName.equals("func_175045_a")) && methodNode.desc.equals("(Lnet/minecraft/client/resources/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V")) {
                methodNode.instructions.insert(renderModelStart());
                methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), renderModelEnd());
            }
        }
    }

    private InsnList renderItemHookInit() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, getHookClass("RenderItemHook")));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, getHookClass("RenderItemHook"), "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/entity/RenderItem", "patcherRenderItemHook", "L" + getHookClass("RenderItemHook;")));
        return list;
    }

    private InsnList renderModelEnd() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/RenderItem", "patcherRenderItemHook", "L" + getHookClass("RenderItemHook;")));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, getHookClass("RenderItemHook"), "renderModelEnd", "()V", false));
        return list;
    }

    private InsnList renderModelStart() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/RenderItem", "patcherRenderItemHook", "L" + getHookClass("RenderItemHook;")));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, getHookClass("RenderItemHook"), "renderModelStart", "(Lnet/minecraft/client/resources/model/IBakedModel;ILnet/minecraft/item/ItemStack;)Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList cancelRendering() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("disableEnchantmentGlint", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }
}
