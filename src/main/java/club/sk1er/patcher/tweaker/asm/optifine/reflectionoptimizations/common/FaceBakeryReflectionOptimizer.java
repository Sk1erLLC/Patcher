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

package club.sk1er.patcher.tweaker.asm.optifine.reflectionoptimizations.common;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class FaceBakeryReflectionOptimizer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.block.model.FaceBakery"};
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
            if (methodNode.name.equals("makeBakedQuad") && methodNode.desc.contains("minecraftforge")) {
                final ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();

                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).name.equals("exists")) {
                        for (int i = 0; i < 17; ++i) {
                            methodNode.instructions.remove(next.getNext());
                        }

                        methodNode.instructions.remove(next.getPrevious());
                        methodNode.instructions.insertBefore(next, fillNormalReflectionOptimization());
                        methodNode.instructions.remove(next);
                        break;
                    }
                }
            } else if (methodNode.name.equals("rotateVertex") && methodNode.desc.contains("minecraftforge")) {
                clearInstructions(methodNode);
                methodNode.instructions.insert(rotateVertexReflectionOptimization());
            }
        }
    }

    private InsnList rotateVertexReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/resources/model/ModelRotation", "X0_Y0", "Lnet/minecraft/client/resources/model/ModelRotation;"));
        LabelNode ifacmpne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ACMPNE, ifacmpne));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new InsnNode(Opcodes.IRETURN));
        list.add(ifacmpne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/model/ITransformation", "getMatrix", "()Ljavax/vecmath/Matrix4f;", true));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/client/ForgeHooksClient", "transform", "(Lorg/lwjgl/util/vector/Vector3f;Ljavax/vecmath/Matrix4f;)V", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 4));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new VarInsnNode(Opcodes.ILOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/client/model/ITransformation", "rotate", "(Lnet/minecraft/util/EnumFacing;I)I", true));
        list.add(new InsnNode(Opcodes.IRETURN));
        return list;
    }

    private InsnList fillNormalReflectionOptimization() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 10));
        list.add(new VarInsnNode(Opcodes.ALOAD, 11));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/minecraftforge/client/ForgeHooksClient",
            "fillNormal",
            "([ILnet/minecraft/util/EnumFacing;)V",
            false));
        return list;
    }
}
