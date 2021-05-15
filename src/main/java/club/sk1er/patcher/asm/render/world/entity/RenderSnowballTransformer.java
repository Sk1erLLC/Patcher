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

package club.sk1er.patcher.asm.render.world.entity;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class RenderSnowballTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderSnowball"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);

            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                method.instructions.insert(checkExistingTicks());
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode node = iterator.next();
                    if (node instanceof FieldInsnNode && node.getOpcode() == Opcodes.GETFIELD) {
                        final String fieldName = mapFieldNameFromNode(node);
                        if (fieldName.equals("playerViewX") || fieldName.equals("field_78732_j")) {
                            method.instructions.insertBefore(node.getPrevious().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "club/sk1er/patcher/asm/external/mods/optifine/RenderTransformer", "checkPerspective",
                                "()F", false));
                            method.instructions.insertBefore(node.getNext(), new InsnNode(Opcodes.FMUL));
                            break;
                        }
                    }
                }

                break;
            }
        }
    }

    private InsnList checkExistingTicks() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("cleanProjectiles", "Z"));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, label));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "field_70173_aa", "I"));
        list.add(new InsnNode(Opcodes.ICONST_2));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPGE, label));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(label);
        return list;
    }
}
