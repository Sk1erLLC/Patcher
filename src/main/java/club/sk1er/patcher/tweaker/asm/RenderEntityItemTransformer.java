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
import org.objectweb.asm.tree.*;

public class RenderEntityItemTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderEntityItem"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/world/entity/culling/EntityCulling", "renderItem", "(Lnet/minecraft/entity/Entity;)Z", false));
                final LabelNode labelNode = new LabelNode();
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                list.add(new InsnNode(Opcodes.RETURN));
                list.add(labelNode);
                method.instructions.insertBefore(method.instructions.getFirst(), list);
                return;
            }
        }
    }
}
