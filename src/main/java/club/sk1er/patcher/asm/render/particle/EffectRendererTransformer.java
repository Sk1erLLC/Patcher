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

package club.sk1er.patcher.asm.render.particle;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

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
                case "renderParticles":
                case "func_78874_a": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    LabelNode ifeq = new LabelNode();
                    int entityfxIndex = -1;

                    for (LocalVariableNode variable : methodNode.localVariables) {
                        if (variable.name.equals("entityfx") || variable.name.equals("var13")) {
                            entityfxIndex = variable.index;
                            break;
                        }
                    }

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("get")) {
                            next = next.getNext().getNext();

                            methodNode.instructions.insertBefore(next.getNext(), determineRender(entityfxIndex, ifeq));
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
                            String methodInsnName = mapMethodNameFromNode(next);

                            if (methodInsnName.equals("tickParticle") || methodInsnName.equals("func_178923_d")) {
                                methodNode.instructions.insertBefore(next.getNext(), checkIfCull(entityfxIndex));
                                break;
                            }
                        }
                    }
                    break;
                }

                case "func_78872_b":
                case "renderLitParticles": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) next).var == 8) {
                            while (next.getPrevious() != null) {
                                methodNode.instructions.remove(next.getPrevious());
                            }

                            methodNode.instructions.insertBefore(next.getNext(), reassignRotation());
                            methodNode.instructions.remove(next);
                            break;
                        }
                    }

                    break;
                }

                case "func_180533_a":
                case "addBlockDestroyEffects":
                case "func_180532_a":
                case "addBlockHitEffects":
                    methodNode.instructions.insert(cancelParticles());
                    break;

                case "func_78873_a":
                case "addEffect": {
                    final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        final AbstractInsnNode next = iterator.next();
                        if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 4000) {
                            methodNode.instructions.set(next, getPatcherSetting("maxParticleLimit", "I"));
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList reassignRotation() {
        InsnList list = new InsnList();
        // unnecessary but just for compatibility
        list.add(new LdcInsnNode(0.017453292F));
        list.add(new VarInsnNode(Opcodes.FSTORE, 3));
        // da real fix
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", "func_178808_b", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", "func_178803_d", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 5));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", "func_178805_e", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 6));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", "func_178807_f", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 7));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/ActiveRenderInfo", "func_178809_c", "()F", false));
        list.add(new VarInsnNode(Opcodes.FSTORE, 8));
        return list;
    }

    private InsnList cancelParticles() {
        InsnList list = new InsnList();
        list.add(getPatcherSetting("disableBlockBreakParticles", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList checkIfCull(int entityfxIndex) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/util/world/render/culling/ParticleCulling", "camera", "Lnet/minecraft/client/renderer/culling/ICamera;"));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        list.add(new VarInsnNode(Opcodes.ALOAD, entityfxIndex));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/patcher/util/world/render/culling/ParticleCulling", "camera", "Lnet/minecraft/client/renderer/culling/ICamera;"));
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

    private InsnList determineRender(int entityfxIndex, LabelNode ifeq) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, entityfxIndex));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/world/render/culling/ParticleCulling", "shouldRender", "(Lnet/minecraft/client/particle/EntityFX;)Z", false));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        return list;
    }
}
