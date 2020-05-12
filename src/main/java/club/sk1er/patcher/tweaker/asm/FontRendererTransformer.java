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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FontRendererTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.FontRenderer"};
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

            if ((methodName.equalsIgnoreCase("renderString") || methodName.equals("func_180455_b")) && methodNode.desc.equals("(Ljava/lang/String;FFIZ)I")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getShadowHook());
                break;
            }
        }
    }

    private InsnList getShadowHook() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ILOAD, 5));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "disableShadowedText", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new LdcInsnNode(0));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifeq);
        return list;
    }
}
