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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BlockRendererDispatcherTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.BlockRendererDispatcher"};
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

            if (methodName.equals("renderBlock") || methodName.equals("func_175018_a")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), dontRender());
                break;
            }
        }
    }

    private InsnList dontRender() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/util/block/BlockUtil", "grassContainer", "Ljava/util/List;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", "func_177230_c", // getBlock
            "()Lnet/minecraft/block/Block;", true));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "removeTallGrass", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(labelNode);
        return list;
    }
}
