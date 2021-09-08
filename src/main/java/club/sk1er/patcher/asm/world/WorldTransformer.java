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

package club.sk1er.patcher.asm.world;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

public class WorldTransformer implements PatcherTransformer {

    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.world.World"};
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

            // todo: how should we convert these into a mixin?
            switch (methodName) {
                case "updateEntityWithOptionalForce":
                case "func_72866_a": {
                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                            if (methodInsnNode.name.equals("getPersistentChunks")) {
                                AbstractInsnNode prevNode = node.getPrevious();
                                methodNode.instructions.insertBefore(prevNode, new VarInsnNode(Opcodes.ALOAD, 0));
                                methodNode.instructions.insertBefore(prevNode, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/World", "field_72995_K", "Z"));
                                methodNode.instructions.insertBefore(prevNode, new InsnNode(Opcodes.ICONST_1));
                                methodNode.instructions.insertBefore(prevNode, new InsnNode(Opcodes.IXOR));
                            }
                        } else if (node.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) node).var == 5) {
                            methodNode.instructions.insertBefore(node, new InsnNode(Opcodes.IAND));
                            break;
                        }
                    }
                    break;
                }

                case "func_72945_a":
                case "getCollidingBoundingBoxes": {
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();

                        if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(0.25D)) {
                            methodNode.instructions.insertBefore(next, filterEntities());
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }

    private InsnList filterEntities() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/entity/item/EntityTNTPrimed"));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/entity/item/EntityFallingBlock"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/entity/item/EntityItem"));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/client/particle/EntityFX"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(ifeq);
        return list;
    }
}
