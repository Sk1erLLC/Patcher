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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RenderPlayerTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderPlayer"};
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

            switch (methodName) {
                case "renderRightArm":
                case "func_177138_b": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode node = iterator.next();

                        if (node instanceof MethodInsnNode) {
                            if (node.getOpcode() == Opcodes.INVOKESPECIAL) {
                                final String methodInsnName = mapMethodNameFromNode(node);
                                if (methodInsnName.equals("setModelVisibilities") || methodInsnName.equals("func_177137_d")) {
                                    methodNode.instructions.insertBefore(node.getNext(), enableBlend());
                                }
                            }
                        } else if (node instanceof FieldInsnNode && node.getOpcode() == Opcodes.PUTFIELD) {
                            final String methodInsnName = mapFieldNameFromNode(node);
                            if (methodInsnName.equals("swingProgress") || methodInsnName.equals("field_78095_p")) {
                                for (int i = 0; i < 5; i++) {
                                    methodNode.instructions.remove(node.getNext());
                                }

                                methodNode.instructions.insertBefore(node.getNext(), newArmLogic());
                            }
                        }
                    }

                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179084_k", "()V", false));
                    break;
                }

                case "doRender":
                case "func_76986_a": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode) {
                            final String methodInsnName = mapMethodNameFromNode(next);
                            if (methodInsnName.equals("doRender") || methodInsnName.equals("func_76986_a")) {
                                methodNode.instructions.insertBefore(next.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179084_k", "()V", false));
                            } else if (methodInsnName.equals("setModelVisibilities") || methodInsnName.equals("func_177137_d")) {
                                methodNode.instructions.insertBefore(next.getNext(), enableBlend());
                            }
                        }
                    }

                    break;
                }

                case "func_177137_d":
                case "setModelVisibilities": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();

                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETFIELD) {
                            final String fieldName = mapFieldNameFromNode(next);
                            if ((fieldName.equals("bipedHeadwear") || fieldName.equals("field_178720_f")) && next.getNext().getOpcode() == Opcodes.ICONST_1) {
                                methodNode.instructions.remove(next.getNext());
                                methodNode.instructions.insertBefore(next.getNext(), checkHatLayer());
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList checkHatLayer() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/entity/player/EnumPlayerModelParts", "HAT", "Lnet/minecraft/entity/player/EnumPlayerModelParts;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/AbstractClientPlayer", "func_175148_a", "(Lnet/minecraft/entity/player/EnumPlayerModelParts;)Z", false));
        return list;
    }

    public static InsnList enableBlend() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179147_l", "()V", false));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 770));
        list.add(new IntInsnNode(Opcodes.SIPUSH, 771));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "func_179120_a", "(IIII)V", false));
        return list;
    }


    private InsnList newArmLogic() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new InsnNode(Opcodes.DUP_X1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelPlayer", "field_78117_n", "Z"));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelPlayer", "field_78093_q", "Z"));
        return list;
    }
}
