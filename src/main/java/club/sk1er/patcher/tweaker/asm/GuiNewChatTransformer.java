package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.config.PatcherConfig;
import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import net.minecraft.client.renderer.GlStateManager;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.ListIterator;

public class GuiNewChatTransformer implements PatcherTransformer {
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

                        if (node instanceof IntInsnNode && ((IntInsnNode) node).operand == 100) {
                            methodNode.instructions.insertBefore(node,
                                new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(),
                                    "chatHistoryLength", "I"));
                            methodNode.instructions.remove(node);
                        }
                    }
                    break;
                }

                case "drawChat":
                case "func_146230_a": {
                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == Opcodes.INVOKESTATIC
                            && node.getPrevious().getOpcode() == Opcodes.ISHL) {
                            LabelNode ifeq = new LabelNode();
                            methodNode.instructions.insert(node, ifeq);
                            AbstractInsnNode prevNode = node;

                            for (int i = 0; i < 15; i++) {
                                prevNode = prevNode.getPrevious();
                            }

                            methodNode.instructions.insertBefore(prevNode,
                                new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(),
                                    "transparentChat", "Z"));
                            methodNode.instructions
                                .insertBefore(prevNode, new JumpInsnNode(Opcodes.IFNE, ifeq));
                            break;
                        }
                    }

                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "club/sk1er/patcher/tweaker/asm/GuiNewChatTransformer",
                            "moveDownAndPushMatrix", "()V", false));
                    methodNode.instructions.insertBefore(methodNode.instructions.getLast().getPrevious(),
                        new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "club/sk1er/patcher/tweaker/asm/GuiNewChatTransformer", "popMatrix", "()V",
                            false));
                    break;
                }

                case "getChatComponent":
                case "func_146236_a": {
                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESTATIC) {
                            String methodInsnName = mapMethodNameFromNode((MethodInsnNode) node);

                            if (methodInsnName.equals("floor_float")) {
                                for (int i = 0; i < 4; ++i) {
                                    node = node.getPrevious();
                                }

                                methodNode.instructions.insertBefore(node, minus12());
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public static void moveDownAndPushMatrix() {
        if (PatcherConfig.chatPosition) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -12, 0);
        }
    }

    public static void popMatrix() {
        if (PatcherConfig.chatPosition) {
            GlStateManager.popMatrix();
        }
    }

    private InsnList minus12() {
        InsnList list = new InsnList();
        LabelNode afterSub = new LabelNode();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "chatPosition", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterSub));
        list.add(new IincInsnNode(7, -12));
        list.add(afterSub);
        return list;
    }
}
