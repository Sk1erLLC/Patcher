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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class MinecraftTransformer implements PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.Minecraft"};
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
            String methodDesc = mapMethodDesc(methodNode);

            if (methodName.equals("toggleFullscreen") || methodName.equals("func_71352_k")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).name.equals("setFullscreen")) {
                        methodNode.instructions.insert(node, resetScreenState());
                        break;
                    }
                }
            }

            if ((methodName.equals("loadWorld") || methodName.equals("func_71353_a")) && methodDesc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).owner.equals("java/lang/System")) {
                        methodNode.instructions.insertBefore(node, setSystemTime());
                        break;
                    }
                }
            }

            // todo: config
            if (methodName.equals("displayGuiScreen")) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node instanceof FieldInsnNode) {
                        if (node.getOpcode() == Opcodes.GETFIELD && node.getNext().getOpcode() == Opcodes.INVOKEVIRTUAL && node.getNext().getNext().getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            methodNode.instructions.remove(node.getPrevious());
                            methodNode.instructions.remove(node.getNext());
                            methodNode.instructions.remove(node.getNext());
                            methodNode.instructions.remove(node);
                        }
                    }
                }
            }
        }
    }

    private InsnList setSystemTime() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "instantWorldSwapping", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new InsnNode(Opcodes.LCONST_0));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/Minecraft", "field_71423_H", "J")); // systemTime
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(ifeq);
        return list;
    }

    private InsnList resetScreenState() {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, getPatcherConfigClass(), "fullscreenFix", "Z"));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
        list.add(ifeq);
        return list;
    }
}
