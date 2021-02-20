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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class LayerHeldItemTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.layers.LayerHeldItem"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof LdcInsnNode) {
                        if (((LdcInsnNode) next).cst.equals(-20.0F)) {
                            for (int i = 0; i < 4; i++) {
                                method.instructions.remove(next.getNext());
                            }

                            method.instructions.remove(next);
                        } else if (((LdcInsnNode) next).cst.equals(0.625F)) {
                            ((LdcInsnNode) next).cst = 0.75F;
                        }

                        break;
                    }
                }

                iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETSTATIC) {
                        // todo maybe make a toggle? maybe someone likes
                        //  the fishing rod not being enchanted in third person
                        final String fieldName = mapFieldNameFromNode(next);
                        if (fieldName.equals("fishing_rod") || fieldName.equals("field_151112_aM")) {
                            next = next.getNext().getNext().getNext().getNext();

                            for (int i = 0; i < 15; i++) {
                                method.instructions.remove(next.getPrevious());
                            }

                            method.instructions.remove(next);
                            break;
                        }
                    }
                }

                break;
            }
        }
    }
}
