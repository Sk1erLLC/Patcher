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

import club.sk1er.patcher.agent.HotReloadable;
import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

@HotReloadable
public class GuiNewChatTransformer implements CommonTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
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
                case "setChatLine":
                case "func_146237_a": {
                    methodNode.instructions.insert(setChatLineHead());
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(), setChatLineReturn());
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();

                        if (node instanceof IntInsnNode && ((IntInsnNode) node).operand == 100) {
                            methodNode.instructions.insertBefore(node, new IntInsnNode(Opcodes.SIPUSH, 32767));
                            methodNode.instructions.remove(node);
                        } else if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESPECIAL &&
                            mapClassName(((MethodInsnNode) node).owner).equals("net/minecraft/client/gui/ChatLine")) {
                            InsnList list = new InsnList();
                            list.add(new InsnNode(Opcodes.DUP));
                            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/chat/ChatHandler", "setChatLine_addToList", "(Lnet/minecraft/client/gui/ChatLine;)V", false));
                            methodNode.instructions.insert(node, list);
                        }
                    }

                    break;
                }

                case "drawChat":
                case "func_146230_a": {
                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node instanceof MethodInsnNode) {
                            final String methodInsnName = mapMethodNameFromNode(node);
                            if (methodInsnName.equals("getLineCount") || methodInsnName.equals("func_146232_i")) {
                                methodNode.instructions.insertBefore(node.getPrevious(),
                                    new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("GuiNewChatHook"), "processMessageQueue", "()V", false));
                            }
                        } else if (node.getOpcode() == Opcodes.INVOKESTATIC && node.getPrevious().getOpcode() == Opcodes.ISHL) {
                            LabelNode ifeq = new LabelNode();
                            methodNode.instructions.insert(node, ifeq);
                            AbstractInsnNode prevNode = node;

                            for (int i = 0; i < 15; i++) {
                                prevNode = prevNode.getPrevious();
                            }

                            methodNode.instructions.insertBefore(prevNode, new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "transparentChat", "Z"));
                            methodNode.instructions.insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, ifeq));
                        } else if (node instanceof JumpInsnNode && node.getOpcode() == Opcodes.IFEQ) {
                            AbstractInsnNode prevNode = node;

                            for (int i = 0; i < 7; i++) {
                                prevNode = prevNode.getNext();
                            }

                            if (prevNode.getOpcode() == Opcodes.ISTORE) {
                                methodNode.instructions.insertBefore(node.getPrevious(),
                                    new MethodInsnNode(Opcodes.INVOKESTATIC, getHooksPackage("GuiNewChatHook"), "drawMessageQueue", "()V", false));
                            }
                        }
                    }


                    break;
                }

                case "getChatComponent":
                case "func_146236_a": {
                    this.changeChatComponentHeight(methodNode);
                    break;
                }

                case "func_146231_a":
                case "clearChatMessages":
                    methodNode.instructions.insert(clearMessageQueue());
            }
        }
    }

    private InsnList clearMessageQueue() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getHooksPackage("GuiNewChatHook"), "messageQueue", "Ljava/util/Deque;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Deque", "clear", "()V", true));
        return list;
    }

    private InsnList setChatLineReturn() {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/chat/ChatHandler", "setChatLineReturn", "()V", false));
        return list;
    }

    private InsnList setChatLineHead() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 4));
        LabelNode ifne = new LabelNode();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/patcher/util/chat/ChatHandler", "setChatLineHead", "(Lnet/minecraft/util/IChatComponent;Z)Z", false));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifne);
        return list;
    }
}
