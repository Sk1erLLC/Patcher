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

package club.sk1er.patcher.tweaker.asm.optifine;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class RendererLivingEntityTransformer implements CommonTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RendererLivingEntity"};
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
            if (methodName.equals("doRender") || methodName.equals("func_76986_a")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                int fIndex = -1;
                int f1Index = -1;
                int f2Index = -1;

                for (LocalVariableNode var : methodNode.localVariables) {
                    switch (var.name) {
                        case "var10":
                        case "f":
                            fIndex = var.index;
                            break;

                        case "var11":
                        case "f1":
                            f1Index = var.index;
                            break;

                        case "var12":
                        case "f2":
                            f2Index = var.index;
                            break;
                    }
                }

                /*
                Find if (shouldSit && entity.ridingEntity instanceof EntityLivingBase)
                    go forward until we find the label we jump to if that statement if false, retract 1 insn, then read f2 = f1 -f
                 */

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof TypeInsnNode) {
                        if (next.getOpcode() == Opcodes.INSTANCEOF && ((TypeInsnNode) next).desc.equals("net/minecraft/entity/EntityLivingBase")) {
                            LabelNode node = null; //Find label
                            while ((next = next.getNext()) != null) {
                                if (next instanceof JumpInsnNode && next.getOpcode() == Opcodes.IFEQ) {
                                    node = ((JumpInsnNode) next).label;
                                    break;
                                }
                            }

                            if (next == null) {
                                return;
                            }

                            LabelNode labelNode = new LabelNode(); //Override final if statement to jump to our new end of block
                            while ((next = next.getNext()) != null) {
                                if (next instanceof JumpInsnNode && ((JumpInsnNode) next).label.equals(node)
                                    && next.getOpcode() == Opcodes.IFLE) {
                                    ((JumpInsnNode) next).label = labelNode;
                                    break;
                                }
                            }

                            if (next == null) {
                                return;
                            }

                            while ((next = next.getNext()) != null) {
                                if (next == node) {
                                    InsnList insnList = new InsnList();
                                    insnList.add(labelNode);
                                    insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "headRotation", "Z"));
                                    LabelNode ifeq = new LabelNode();
                                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
                                    insnList.add(new VarInsnNode(Opcodes.FLOAD, f1Index));
                                    insnList.add(new VarInsnNode(Opcodes.FLOAD, fIndex));
                                    insnList.add(new InsnNode(Opcodes.FSUB));
                                    insnList.add(new VarInsnNode(Opcodes.FSTORE, f2Index));
                                    insnList.add(ifeq);
                                    methodNode.instructions.insertBefore(next, insnList);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (methodName.equals("renderName") || methodName.equals("func_177067_a")) {
                makeNametagTransparent(methodNode);
                makeNametagShadowed(methodNode);
            }
        }
    }
}
