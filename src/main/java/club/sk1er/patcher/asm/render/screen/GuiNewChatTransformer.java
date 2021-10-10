package club.sk1er.patcher.asm.render.screen;

import club.sk1er.patcher.tweaker.transform.CommonTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

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
                    ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();

                        if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESPECIAL &&
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
                        if (node instanceof JumpInsnNode && node.getOpcode() == Opcodes.IFEQ) {
                            AbstractInsnNode prevNode = node;

                            for (int i = 0; i < 7; i++) {
                                prevNode = prevNode.getNext();
                            }

                            if (prevNode.getOpcode() == Opcodes.ISTORE) {
                                methodNode.instructions.insertBefore(node.getPrevious(),
                                    new MethodInsnNode(Opcodes.INVOKESTATIC, getHookClass("GuiNewChatHook"), "drawMessageQueue", "()V", false));
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
            }
        }
    }
}
