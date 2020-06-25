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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class EffectRendererTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.particle.EffectRenderer"};
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
                case "updateEffects":
                case "func_78873_a":
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "club/sk1er/patcher/util/world/entity/culling/EntityCulling",
                        "begin",
                        "()V",
                        false));
                    break;
                case "renderParticles":
                case "func_78874_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    LabelNode ifeq = new LabelNode();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("get")) {
                            next = next.getNext().getNext();

                            methodNode.instructions.insertBefore(next.getNext(), determineRender(ifeq));
                        } else if (next instanceof InsnNode && next.getOpcode() == Opcodes.ATHROW) {
                            methodNode.instructions.insertBefore(next.getNext(), ifeq);
                        }
                    }
                    break;
                }
                case "updateEffectAlphaLayer":
                case "func_178925_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    int entityfxIndex = -1;

                    for (LocalVariableNode var : methodNode.localVariables) {
                        if (var.name.equals("entityfx") || var.name.equals("var4")) {
                            entityfxIndex = var.index;
                            break;
                        }
                    }

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) next);

                            if (methodInsnName.equals("tickParticle") || methodInsnName.equals("func_178923_d")) {
                                methodNode.instructions.insertBefore(next.getNext(), checkIfCull(entityfxIndex));
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private InsnList checkIfCull(int entityfxIndex) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/util/world/particles/ParticleCulling", "camera", "Lnet/minecraft/client/renderer/culling/ICamera;"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "cullParticles", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, entityfxIndex));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/util/world/particles/ParticleCulling", "camera", "Lnet/minecraft/client/renderer/culling/ICamera;"));
        list.add(new VarInsnNode(Opcodes.ALOAD, entityfxIndex));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/particle/EntityFX", "func_174813_aQ", "()Lnet/minecraft/util/AxisAlignedBB;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/client/renderer/culling/ICamera", "func_78546_a", "(Lnet/minecraft/util/AxisAlignedBB;)Z", true));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.FCONST_1));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new LdcInsnNode(-1.0f));
        list.add(gotoInsn);
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/particle/EntityFX", "field_70140_Q", "F"));
        list.add(labelNode);
        return list;
    }

    private InsnList determineRender(LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 14));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/world/particles/ParticleCulling", "shouldRender", "(Lnet/minecraft/client/particle/EntityFX;)Z", false));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        return list;
    }
}
